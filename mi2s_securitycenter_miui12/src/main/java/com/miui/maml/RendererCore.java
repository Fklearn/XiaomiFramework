package com.miui.maml;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.util.Log;
import com.miui.maml.RendererController;
import java.lang.ref.WeakReference;

public class RendererCore {
    private static final String LOG_TAG = "RendererCore";
    private boolean mCleaned;
    private MultipleRenderable mMultipleRenderable;
    private WeakReference<OnReleaseListener> mOnReleaseListener;
    private boolean mReleased;
    private ScreenElementRoot mRoot;
    private RenderThread mThread;

    public interface OnReleaseListener {
        boolean OnRendererCoreReleased(RendererCore rendererCore);
    }

    public RendererCore(ScreenElementRoot screenElementRoot, RenderThread renderThread) {
        this(screenElementRoot, renderThread, true);
    }

    public RendererCore(ScreenElementRoot screenElementRoot, RenderThread renderThread, boolean z) {
        this.mMultipleRenderable = new MultipleRenderable();
        this.mThread = renderThread;
        this.mRoot = screenElementRoot;
        this.mRoot.setRenderControllerRenderable(this.mMultipleRenderable);
        this.mRoot.selfInit();
        if (z) {
            attach(renderThread);
        }
    }

    public synchronized void addRenderable(RendererController.IRenderable iRenderable) {
        if (!this.mCleaned) {
            this.mMultipleRenderable.add(iRenderable);
            Log.d(LOG_TAG, "add: " + iRenderable + " size:" + this.mMultipleRenderable.size());
            this.mRoot.selfResume();
            this.mReleased = false;
        }
    }

    public void attach(RenderThread renderThread) {
        this.mThread = renderThread;
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            screenElementRoot.attachToRenderThread(this.mThread);
            this.mRoot.requestUpdate();
        }
    }

    public void cleanUp() {
        this.mCleaned = true;
        Log.d(LOG_TAG, "cleanUp: " + toString());
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            screenElementRoot.detachFromRenderThread(this.mThread);
            this.mRoot.selfFinish();
            this.mRoot = null;
        }
    }

    /* access modifiers changed from: protected */
    public void finalize() {
        cleanUp();
        super.finalize();
    }

    public ScreenElementRoot getRoot() {
        return this.mRoot;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x002f, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void pauseRenderable(com.miui.maml.RendererController.IRenderable r3) {
        /*
            r2 = this;
            monitor-enter(r2)
            boolean r0 = r2.mCleaned     // Catch:{ all -> 0x0030 }
            if (r0 == 0) goto L_0x0007
            monitor-exit(r2)
            return
        L_0x0007:
            com.miui.maml.MultipleRenderable r0 = r2.mMultipleRenderable     // Catch:{ all -> 0x0030 }
            int r3 = r0.pause(r3)     // Catch:{ all -> 0x0030 }
            if (r3 != 0) goto L_0x002e
            java.lang.String r3 = "RendererCore"
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0030 }
            r0.<init>()     // Catch:{ all -> 0x0030 }
            java.lang.String r1 = "self pause: "
            r0.append(r1)     // Catch:{ all -> 0x0030 }
            java.lang.String r1 = r2.toString()     // Catch:{ all -> 0x0030 }
            r0.append(r1)     // Catch:{ all -> 0x0030 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0030 }
            android.util.Log.d(r3, r0)     // Catch:{ all -> 0x0030 }
            com.miui.maml.ScreenElementRoot r3 = r2.mRoot     // Catch:{ all -> 0x0030 }
            r3.selfPause()     // Catch:{ all -> 0x0030 }
        L_0x002e:
            monitor-exit(r2)
            return
        L_0x0030:
            r3 = move-exception
            monitor-exit(r2)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.RendererCore.pauseRenderable(com.miui.maml.RendererController$IRenderable):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0062, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void removeRenderable(com.miui.maml.RendererController.IRenderable r4) {
        /*
            r3 = this;
            monitor-enter(r3)
            boolean r0 = r3.mCleaned     // Catch:{ all -> 0x0063 }
            if (r0 == 0) goto L_0x0007
            monitor-exit(r3)
            return
        L_0x0007:
            com.miui.maml.MultipleRenderable r0 = r3.mMultipleRenderable     // Catch:{ all -> 0x0063 }
            r0.remove(r4)     // Catch:{ all -> 0x0063 }
            java.lang.String r0 = "RendererCore"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0063 }
            r1.<init>()     // Catch:{ all -> 0x0063 }
            java.lang.String r2 = "remove: "
            r1.append(r2)     // Catch:{ all -> 0x0063 }
            r1.append(r4)     // Catch:{ all -> 0x0063 }
            java.lang.String r4 = " size:"
            r1.append(r4)     // Catch:{ all -> 0x0063 }
            com.miui.maml.MultipleRenderable r4 = r3.mMultipleRenderable     // Catch:{ all -> 0x0063 }
            int r4 = r4.size()     // Catch:{ all -> 0x0063 }
            r1.append(r4)     // Catch:{ all -> 0x0063 }
            java.lang.String r4 = r1.toString()     // Catch:{ all -> 0x0063 }
            android.util.Log.d(r0, r4)     // Catch:{ all -> 0x0063 }
            com.miui.maml.MultipleRenderable r4 = r3.mMultipleRenderable     // Catch:{ all -> 0x0063 }
            int r4 = r4.size()     // Catch:{ all -> 0x0063 }
            if (r4 != 0) goto L_0x0061
            com.miui.maml.ScreenElementRoot r4 = r3.mRoot     // Catch:{ all -> 0x0063 }
            r4.selfPause()     // Catch:{ all -> 0x0063 }
            boolean r4 = r3.mReleased     // Catch:{ all -> 0x0063 }
            if (r4 != 0) goto L_0x005e
            java.lang.ref.WeakReference<com.miui.maml.RendererCore$OnReleaseListener> r4 = r3.mOnReleaseListener     // Catch:{ all -> 0x0063 }
            if (r4 == 0) goto L_0x005e
            java.lang.ref.WeakReference<com.miui.maml.RendererCore$OnReleaseListener> r4 = r3.mOnReleaseListener     // Catch:{ all -> 0x0063 }
            java.lang.Object r4 = r4.get()     // Catch:{ all -> 0x0063 }
            if (r4 == 0) goto L_0x005e
            java.lang.ref.WeakReference<com.miui.maml.RendererCore$OnReleaseListener> r4 = r3.mOnReleaseListener     // Catch:{ all -> 0x0063 }
            java.lang.Object r4 = r4.get()     // Catch:{ all -> 0x0063 }
            com.miui.maml.RendererCore$OnReleaseListener r4 = (com.miui.maml.RendererCore.OnReleaseListener) r4     // Catch:{ all -> 0x0063 }
            boolean r4 = r4.OnRendererCoreReleased(r3)     // Catch:{ all -> 0x0063 }
            if (r4 == 0) goto L_0x005e
            r3.cleanUp()     // Catch:{ all -> 0x0063 }
        L_0x005e:
            r4 = 1
            r3.mReleased = r4     // Catch:{ all -> 0x0063 }
        L_0x0061:
            monitor-exit(r3)
            return
        L_0x0063:
            r4 = move-exception
            monitor-exit(r3)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.RendererCore.removeRenderable(com.miui.maml.RendererController$IRenderable):void");
    }

    public void render(Canvas canvas) {
        if (!this.mCleaned && this.mThread.isStarted()) {
            this.mRoot.render(canvas);
        }
    }

    public synchronized void resumeRenderable(RendererController.IRenderable iRenderable) {
        if (!this.mCleaned) {
            this.mMultipleRenderable.resume(iRenderable);
            Log.d(LOG_TAG, "self resume: " + toString());
            this.mRoot.selfResume();
        }
    }

    public void setColorFilter(ColorFilter colorFilter) {
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            screenElementRoot.setColorFilter(colorFilter);
        }
    }

    public void setOnReleaseListener(OnReleaseListener onReleaseListener) {
        this.mOnReleaseListener = new WeakReference<>(onReleaseListener);
    }
}
