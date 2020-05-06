package com.miui.maml;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import java.util.ArrayList;

public class RenderThread extends Thread {
    private static final String LOG_TAG = "RenderThread";
    private static RenderThread sGlobalThread;
    private static Object sGlobalThreadLock = new Object();
    private CommandThreadHandler mCmdHanlder;
    private HandlerThread mCmdThread;
    private boolean mPaused = true;
    private ArrayList<RendererController> mRendererControllerList = new ArrayList<>();
    private Object mResumeSignal = new Object();
    private boolean mSignaled;
    private Object mSleepSignal = new Object();
    private boolean mStarted;
    private boolean mStop;

    private class CommandThreadHandler extends Handler {
        private static final int MSG_PAUSE = 0;
        private static final int MSG_RESUME = 1;

        public CommandThreadHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            RenderThread renderThread;
            int i = message.what;
            boolean z = true;
            if (i == 0) {
                renderThread = RenderThread.this;
            } else if (i == 1) {
                renderThread = RenderThread.this;
                z = false;
            } else {
                return;
            }
            renderThread.setPausedImpl(z);
        }

        public void setPause(boolean z) {
            Message message = new Message();
            message.what = z ^ true ? 1 : 0;
            sendMessage(message);
        }
    }

    public RenderThread() {
        super("MAML RenderThread");
        initCmdThread();
    }

    public RenderThread(RendererController rendererController) {
        super("MAML RenderThread");
        addRendererController(rendererController);
        initCmdThread();
    }

    private void doFinish() {
        synchronized (this.mRendererControllerList) {
            if (this.mRendererControllerList.size() != 0) {
                int size = this.mRendererControllerList.size();
                for (int i = 0; i < size; i++) {
                    this.mRendererControllerList.get(i).finish();
                }
            }
        }
    }

    private void doInit() {
        synchronized (this.mRendererControllerList) {
            if (this.mRendererControllerList.size() != 0) {
                int size = this.mRendererControllerList.size();
                for (int i = 0; i < size; i++) {
                    RendererController rendererController = this.mRendererControllerList.get(i);
                    rendererController.init();
                    rendererController.requestUpdate();
                }
            }
        }
    }

    private void doPause() {
        synchronized (this.mRendererControllerList) {
            if (this.mRendererControllerList.size() != 0) {
                int size = this.mRendererControllerList.size();
                for (int i = 0; i < size; i++) {
                    this.mRendererControllerList.get(i).pause();
                }
            }
        }
    }

    private void doResume() {
        synchronized (this.mRendererControllerList) {
            if (this.mRendererControllerList.size() != 0) {
                int size = this.mRendererControllerList.size();
                for (int i = 0; i < size; i++) {
                    this.mRendererControllerList.get(i).resume();
                }
            }
        }
    }

    public static RenderThread globalThread() {
        return globalThread(false);
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(7:2|3|(1:5)|(2:9|10)|11|12|13) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x001d */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.miui.maml.RenderThread globalThread(boolean r2) {
        /*
            java.lang.Object r0 = sGlobalThreadLock
            monitor-enter(r0)
            com.miui.maml.RenderThread r1 = sGlobalThread     // Catch:{ all -> 0x0021 }
            if (r1 != 0) goto L_0x000e
            com.miui.maml.RenderThread r1 = new com.miui.maml.RenderThread     // Catch:{ all -> 0x0021 }
            r1.<init>()     // Catch:{ all -> 0x0021 }
            sGlobalThread = r1     // Catch:{ all -> 0x0021 }
        L_0x000e:
            if (r2 == 0) goto L_0x001d
            com.miui.maml.RenderThread r2 = sGlobalThread     // Catch:{ all -> 0x0021 }
            boolean r2 = r2.isStarted()     // Catch:{ all -> 0x0021 }
            if (r2 != 0) goto L_0x001d
            com.miui.maml.RenderThread r2 = sGlobalThread     // Catch:{ IllegalThreadStateException -> 0x001d }
            r2.start()     // Catch:{ IllegalThreadStateException -> 0x001d }
        L_0x001d:
            com.miui.maml.RenderThread r2 = sGlobalThread     // Catch:{ all -> 0x0021 }
            monitor-exit(r0)     // Catch:{ all -> 0x0021 }
            return r2
        L_0x0021:
            r2 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0021 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.RenderThread.globalThread(boolean):com.miui.maml.RenderThread");
    }

    public static void globalThreadStop() {
        synchronized (sGlobalThreadLock) {
            if (sGlobalThread != null) {
                sGlobalThread.setStop();
                sGlobalThread = null;
            }
        }
    }

    private void initCmdThread() {
        this.mCmdThread = new HandlerThread("cmd");
        this.mCmdThread.start();
        this.mCmdHanlder = new CommandThreadHandler(this.mCmdThread.getLooper());
    }

    /* access modifiers changed from: private */
    public void setPausedImpl(boolean z) {
        if (this.mStop) {
            signal();
        }
        if (this.mPaused != z) {
            synchronized (this.mResumeSignal) {
                this.mPaused = z;
                if (!z) {
                    this.mResumeSignal.notify();
                }
            }
            signal();
        }
    }

    private final void waitSleep(long j) {
        if (!this.mSignaled && j > 0) {
            synchronized (this.mSleepSignal) {
                if (!this.mSignaled) {
                    try {
                        this.mSleepSignal.wait(j);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void waiteForResume() {
        try {
            this.mResumeSignal.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addRendererController(RendererController rendererController) {
        synchronized (this.mRendererControllerList) {
            if (this.mRendererControllerList.contains(rendererController)) {
                Log.w(LOG_TAG, "addRendererController: RendererController already exists");
                return;
            }
            rendererController.setRenderThread(this);
            this.mRendererControllerList.add(rendererController);
            setPaused(false);
        }
    }

    public boolean isStarted() {
        return this.mStarted;
    }

    public void removeRendererController(RendererController rendererController) {
        synchronized (this.mRendererControllerList) {
            this.mRendererControllerList.remove(rendererController);
            rendererController.setRenderThread((RenderThread) null);
        }
    }

    public void run() {
        String str;
        Log.i(LOG_TAG, "RenderThread started");
        try {
            doInit();
            this.mStarted = true;
            while (true) {
                if (this.mStop) {
                    break;
                }
                if (this.mPaused) {
                    synchronized (this.mResumeSignal) {
                        if (this.mPaused) {
                            doPause();
                            Log.i(LOG_TAG, "RenderThread paused, waiting for signal");
                            waiteForResume();
                            Log.i(LOG_TAG, "RenderThread resumed");
                            doResume();
                        }
                    }
                }
                if (this.mStop) {
                    break;
                }
                long elapsedRealtime = SystemClock.elapsedRealtime();
                synchronized (this.mRendererControllerList) {
                    int size = this.mRendererControllerList.size();
                    long j = Long.MAX_VALUE;
                    int i = 0;
                    boolean z = true;
                    while (true) {
                        if (i >= size) {
                            break;
                        } else if (this.mPaused) {
                            break;
                        } else {
                            RendererController rendererController = this.mRendererControllerList.get(i);
                            if (!rendererController.isSelfPaused() || rendererController.hasRunnable()) {
                                if (!rendererController.hasInited()) {
                                    rendererController.init();
                                }
                                long updateIfNeeded = rendererController.updateIfNeeded(elapsedRealtime);
                                if (updateIfNeeded < j) {
                                    z = false;
                                    j = updateIfNeeded;
                                } else {
                                    z = false;
                                }
                            }
                            i++;
                        }
                    }
                    if (size != 0) {
                        if (!z) {
                            waitSleep(j);
                            this.mSignaled = false;
                        }
                    }
                    this.mPaused = true;
                    Log.i(LOG_TAG, "All controllers paused.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            str = e.toString();
        } catch (OutOfMemoryError e2) {
            e2.printStackTrace();
            str = e2.toString();
        }
        doFinish();
        this.mCmdThread.quit();
        Log.i(LOG_TAG, "RenderThread stopped");
        Log.e(LOG_TAG, str);
        doFinish();
        this.mCmdThread.quit();
        Log.i(LOG_TAG, "RenderThread stopped");
    }

    public void setPaused(boolean z) {
        this.mCmdHanlder.setPause(z);
    }

    public void setStop() {
        this.mStop = true;
        setPaused(false);
    }

    public void signal() {
        if (!this.mSignaled) {
            synchronized (this.mSleepSignal) {
                this.mSignaled = true;
                this.mSleepSignal.notify();
            }
        }
    }
}
