package com.miui.maml;

import android.util.Log;
import android.view.MotionEvent;
import com.miui.maml.FramerateTokenList;
import com.miui.maml.elements.FramerateController;
import com.miui.maml.util.HideSdkDependencyUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class RendererController implements FramerateTokenList.FramerateChangeListener {
    private static final String LOG_TAG = "RendererController";
    private static final int MAX_MSG_COUNT = 3;
    private float mCurFramerate;
    private long mFrameTime;
    private ArrayList<FramerateController> mFramerateControllers;
    private FramerateTokenList mFramerateTokenList;
    private boolean mInited;
    private long mLastUpdateSystemTime;
    private Listener mListener;
    private byte[] mLock;
    private Object mMsgLock;
    private LinkedList<MotionEvent> mMsgQueue;
    private boolean mNeedReset;
    private boolean mPaused;
    private boolean mPendingRender;
    private ArrayList<Runnable> mReadRunnableQueue;
    private RenderThread mRenderThread;
    private boolean mSelfPaused;
    private boolean mShouldUpdate;
    private float mTouchX;
    private float mTouchY;
    private ArrayList<Runnable> mWriteRunnableQueue;
    private Object mWriteRunnableQueueLock;

    public static abstract class EmptyListener implements Listener {
        public void doRender() {
        }

        public void finish() {
        }

        public void init() {
        }

        public void onHover(MotionEvent motionEvent) {
        }

        public void onTouch(MotionEvent motionEvent) {
        }

        public void pause() {
        }

        public void resume() {
        }

        public void tick(long j) {
        }
    }

    public interface IRenderable {
        void doRender();
    }

    public interface ISelfUpdateRenderable extends IRenderable {
        void forceUpdate();

        void triggerUpdate();
    }

    public interface Listener extends ISelfUpdateRenderable {
        void finish();

        void init();

        void onHover(MotionEvent motionEvent);

        void onTouch(MotionEvent motionEvent);

        void pause();

        void resume();

        void tick(long j);
    }

    public RendererController() {
        this.mFramerateControllers = new ArrayList<>();
        this.mFramerateTokenList = new FramerateTokenList();
        this.mSelfPaused = true;
        this.mLock = new byte[0];
        this.mFrameTime = Long.MAX_VALUE;
        this.mMsgLock = new Object();
        this.mTouchX = -1.0f;
        this.mTouchY = -1.0f;
        this.mWriteRunnableQueue = new ArrayList<>();
        this.mReadRunnableQueue = new ArrayList<>();
        this.mWriteRunnableQueueLock = new Object();
        this.mFramerateTokenList = new FramerateTokenList(this);
    }

    public RendererController(Listener listener) {
        this();
        setListener(listener);
    }

    private void runRunnables() {
        if (!this.mNeedReset) {
            synchronized (this.mWriteRunnableQueueLock) {
                ArrayList<Runnable> arrayList = this.mWriteRunnableQueue;
                this.mWriteRunnableQueue = this.mReadRunnableQueue;
                this.mReadRunnableQueue = arrayList;
            }
            int size = this.mReadRunnableQueue.size();
            for (int i = 0; i < size; i++) {
                this.mReadRunnableQueue.get(i).run();
            }
            this.mReadRunnableQueue.clear();
        }
    }

    public void addFramerateController(FramerateController framerateController) {
        if (!this.mFramerateControllers.contains(framerateController)) {
            this.mFramerateControllers.add(framerateController);
        }
    }

    public final FramerateTokenList.FramerateToken createToken(String str) {
        return this.mFramerateTokenList.createToken(str);
    }

    public final void doRender() {
        Listener listener = this.mListener;
        if (listener != null) {
            this.mPendingRender = true;
            listener.doRender();
        }
    }

    public final void doneRender() {
        this.mPendingRender = false;
        triggerUpdate();
    }

    public void finish() {
        synchronized (this.mLock) {
            if (this.mInited) {
                if (this.mListener != null) {
                    try {
                        this.mListener.finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(LOG_TAG, e.toString());
                    }
                }
                synchronized (this.mMsgLock) {
                    if (this.mMsgQueue != null) {
                        while (this.mMsgQueue.size() > 0) {
                            this.mMsgQueue.poll().recycle();
                        }
                    }
                }
                synchronized (this.mWriteRunnableQueueLock) {
                    this.mWriteRunnableQueue.clear();
                }
                this.mInited = false;
                this.mFramerateTokenList.clear();
            }
        }
    }

    public void forceUpdate() {
        RenderThread renderThread = this.mRenderThread;
        if (renderThread != null) {
            renderThread.signal();
        }
        Listener listener = this.mListener;
        if (listener != null) {
            listener.forceUpdate();
        }
    }

    public final MotionEvent getMessage() {
        MotionEvent motionEvent = null;
        if (this.mMsgQueue == null) {
            return null;
        }
        synchronized (this.mMsgLock) {
            if (this.mMsgQueue != null) {
                motionEvent = this.mMsgQueue.poll();
            }
        }
        return motionEvent;
    }

    public final boolean hasInited() {
        return this.mInited;
    }

    public final boolean hasMessage() {
        boolean z = false;
        if (this.mMsgQueue == null) {
            return false;
        }
        synchronized (this.mMsgLock) {
            if (this.mMsgQueue != null) {
                if (this.mMsgQueue.size() > 0) {
                    z = true;
                }
            }
        }
        return z;
    }

    public final boolean hasRunnable() {
        boolean z;
        synchronized (this.mWriteRunnableQueueLock) {
            z = !this.mWriteRunnableQueue.isEmpty();
        }
        return z;
    }

    public void init() {
        synchronized (this.mLock) {
            if (!this.mInited) {
                if (this.mListener != null) {
                    try {
                        this.mListener.init();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(LOG_TAG, e.toString());
                    }
                }
                this.mInited = true;
            }
        }
    }

    public final boolean isSelfPaused() {
        return this.mSelfPaused;
    }

    public void onFrameRateChage(float f, float f2) {
        if (f2 > 0.0f) {
            triggerUpdate();
        }
    }

    public void onHover(MotionEvent motionEvent) {
        Listener listener;
        String str;
        if (motionEvent != null && (listener = this.mListener) != null) {
            try {
                listener.onHover(motionEvent);
                return;
            } catch (Exception e) {
                e.printStackTrace();
                str = e.toString();
            } catch (OutOfMemoryError e2) {
                e2.printStackTrace();
                str = e2.toString();
            }
        } else {
            return;
        }
        Log.e(LOG_TAG, str);
    }

    public void onTouch(MotionEvent motionEvent) {
        Listener listener;
        String str;
        if (motionEvent != null && (listener = this.mListener) != null) {
            try {
                listener.onTouch(motionEvent);
                return;
            } catch (Exception e) {
                e.printStackTrace();
                str = e.toString();
            } catch (OutOfMemoryError e2) {
                e2.printStackTrace();
                str = e2.toString();
            }
        } else {
            return;
        }
        Log.e(LOG_TAG, str);
    }

    public void pause() {
        if (this.mInited) {
            synchronized (this.mLock) {
                this.mPaused = true;
                if (!this.mSelfPaused && this.mListener != null) {
                    this.mListener.pause();
                }
            }
            this.mPendingRender = false;
        }
    }

    public final boolean pendingRender() {
        return this.mPendingRender;
    }

    public void postMessage(MotionEvent motionEvent) {
        synchronized (this.mMsgLock) {
            if (this.mMsgQueue == null) {
                this.mMsgQueue = new LinkedList<>();
            }
            if (!(motionEvent.getActionMasked() == 2 && motionEvent.getX() == this.mTouchX && motionEvent.getY() == this.mTouchY)) {
                this.mMsgQueue.add(motionEvent);
                this.mTouchX = motionEvent.getX();
                this.mTouchY = motionEvent.getY();
            }
            if (this.mMsgQueue.size() > 3) {
                MotionEvent motionEvent2 = null;
                Iterator it = this.mMsgQueue.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    MotionEvent motionEvent3 = (MotionEvent) it.next();
                    if (motionEvent3.getActionMasked() == 2) {
                        motionEvent2 = motionEvent3;
                        break;
                    }
                }
                if (motionEvent2 != null) {
                    this.mMsgQueue.remove(motionEvent2);
                    motionEvent2.recycle();
                }
            }
        }
        forceUpdate();
    }

    public void postRunnable(Runnable runnable) {
        if (runnable != null) {
            synchronized (this.mWriteRunnableQueueLock) {
                if (!this.mWriteRunnableQueue.contains(runnable)) {
                    this.mWriteRunnableQueue.add(runnable);
                }
            }
            requestUpdate();
            return;
        }
        throw new NullPointerException("postRunnable null");
    }

    public void postRunnableAtFrontOfQueue(Runnable runnable) {
        if (runnable != null) {
            synchronized (this.mWriteRunnableQueueLock) {
                if (!this.mWriteRunnableQueue.contains(runnable)) {
                    this.mWriteRunnableQueue.add(0, runnable);
                }
            }
            requestUpdate();
            return;
        }
        throw new NullPointerException("postRunnable null");
    }

    public void removeFramerateController(FramerateController framerateController) {
        this.mFramerateControllers.remove(framerateController);
    }

    public final void removeToken(FramerateTokenList.FramerateToken framerateToken) {
        this.mFramerateTokenList.removeToken(framerateToken);
    }

    public final void requestUpdate() {
        this.mShouldUpdate = true;
        forceUpdate();
    }

    public void resume() {
        if (this.mInited) {
            synchronized (this.mLock) {
                this.mPaused = false;
                if (!this.mSelfPaused && this.mListener != null) {
                    this.mListener.resume();
                }
            }
        }
    }

    public void selfPause() {
        if (this.mInited) {
            synchronized (this.mLock) {
                if (!this.mSelfPaused) {
                    this.mSelfPaused = true;
                    if (!this.mPaused && this.mListener != null) {
                        this.mListener.pause();
                    }
                }
            }
            this.mPendingRender = false;
        }
    }

    public void selfResume() {
        if (this.mInited) {
            synchronized (this.mLock) {
                if (this.mSelfPaused) {
                    this.mSelfPaused = false;
                    if (!this.mPaused && this.mListener != null) {
                        this.mListener.resume();
                    }
                }
            }
            RenderThread renderThread = this.mRenderThread;
            if (renderThread != null) {
                renderThread.setPaused(false);
            }
        }
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public void setNeedReset(boolean z) {
        this.mNeedReset = z;
    }

    public void setRenderThread(RenderThread renderThread) {
        this.mRenderThread = renderThread;
    }

    public void tick(long j) {
        this.mShouldUpdate = false;
        Listener listener = this.mListener;
        if (listener != null) {
            try {
                listener.tick(j);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(LOG_TAG, e.toString());
            }
        }
        this.mLastUpdateSystemTime = j;
    }

    public void triggerUpdate() {
        RenderThread renderThread = this.mRenderThread;
        if (renderThread != null) {
            renderThread.signal();
        }
        Listener listener = this.mListener;
        if (listener != null) {
            listener.triggerUpdate();
        }
    }

    public long update(long j) {
        long updateFramerate = updateFramerate(j);
        boolean hasRunnable = hasRunnable();
        if (this.mPendingRender && !hasRunnable) {
            return updateFramerate;
        }
        runRunnables();
        MotionEvent message = getMessage();
        if (message != null) {
            if (HideSdkDependencyUtils.MotionEvent_isTouchEvent(message)) {
                onTouch(message);
            } else {
                onHover(message);
            }
        }
        tick(j);
        doRender();
        if (this.mShouldUpdate || hasMessage()) {
            return 0;
        }
        return updateFramerate;
    }

    public final long updateFramerate(long j) {
        int size = this.mFramerateControllers.size();
        long j2 = Long.MAX_VALUE;
        long j3 = Long.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            long updateFramerate = this.mFramerateControllers.get(i).updateFramerate(j);
            if (updateFramerate < j3) {
                j3 = updateFramerate;
            }
        }
        float framerate = this.mFramerateTokenList.getFramerate();
        float f = this.mCurFramerate;
        if (f != framerate) {
            if (f >= 1.0f && framerate < 1.0f) {
                requestUpdate();
            }
            this.mCurFramerate = framerate;
            if (framerate != 0.0f) {
                j2 = (long) (1000.0f / framerate);
            }
            this.mFrameTime = j2;
        }
        long j4 = this.mFrameTime;
        return j4 < j3 ? j4 : j3;
    }

    public long updateIfNeeded(long j) {
        long updateFramerate = updateFramerate(j);
        long j2 = this.mFrameTime;
        long j3 = j2 < Long.MAX_VALUE ? j2 - (j - this.mLastUpdateSystemTime) : Long.MAX_VALUE;
        boolean hasRunnable = hasRunnable();
        if (j3 > 0 && !this.mShouldUpdate && !hasMessage() && !hasRunnable) {
            return j3 < updateFramerate ? j3 : updateFramerate;
        }
        if (this.mPendingRender && !hasRunnable) {
            return updateFramerate;
        }
        runRunnables();
        MotionEvent message = getMessage();
        if (message != null) {
            if (HideSdkDependencyUtils.MotionEvent_isTouchEvent(message)) {
                onTouch(message);
            } else {
                onHover(message);
            }
        }
        tick(j);
        doRender();
        if (this.mShouldUpdate || hasMessage()) {
            return 0;
        }
        return updateFramerate;
    }
}
