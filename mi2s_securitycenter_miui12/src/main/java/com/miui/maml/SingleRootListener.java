package com.miui.maml;

import android.view.MotionEvent;
import com.miui.maml.RendererController;
import java.lang.ref.WeakReference;

public class SingleRootListener implements RendererController.Listener {
    private static final String LOG_TAG = "SingleRootListener";
    private WeakReference<RendererController.IRenderable> mRenderable;
    private ScreenElementRoot mRoot;

    public SingleRootListener(ScreenElementRoot screenElementRoot, RendererController.IRenderable iRenderable) {
        setRoot(screenElementRoot);
        setRenderable(iRenderable);
    }

    public void doRender() {
        WeakReference<RendererController.IRenderable> weakReference = this.mRenderable;
        RendererController.IRenderable iRenderable = weakReference != null ? (RendererController.IRenderable) weakReference.get() : null;
        if (iRenderable != null) {
            iRenderable.doRender();
        }
    }

    public void finish() {
        this.mRoot.finish();
    }

    public void forceUpdate() {
        WeakReference<RendererController.IRenderable> weakReference = this.mRenderable;
        RendererController.IRenderable iRenderable = weakReference != null ? (RendererController.IRenderable) weakReference.get() : null;
        if (iRenderable != null && (iRenderable instanceof RendererController.ISelfUpdateRenderable)) {
            ((RendererController.ISelfUpdateRenderable) iRenderable).forceUpdate();
        }
    }

    public void init() {
        this.mRoot.init();
    }

    public void onHover(MotionEvent motionEvent) {
        this.mRoot.onHover(motionEvent);
        motionEvent.recycle();
    }

    public void onTouch(MotionEvent motionEvent) {
        this.mRoot.onTouch(motionEvent);
        motionEvent.recycle();
    }

    public void pause() {
        this.mRoot.pause();
    }

    public void resume() {
        this.mRoot.resume();
    }

    public void setRenderable(RendererController.IRenderable iRenderable) {
        this.mRenderable = iRenderable != null ? new WeakReference<>(iRenderable) : null;
    }

    public void setRoot(ScreenElementRoot screenElementRoot) {
        if (screenElementRoot != null) {
            this.mRoot = screenElementRoot;
            return;
        }
        throw new NullPointerException("root is null");
    }

    public void tick(long j) {
        this.mRoot.tick(j);
    }

    public void triggerUpdate() {
        WeakReference<RendererController.IRenderable> weakReference = this.mRenderable;
        RendererController.IRenderable iRenderable = weakReference != null ? (RendererController.IRenderable) weakReference.get() : null;
        if (iRenderable != null && (iRenderable instanceof RendererController.ISelfUpdateRenderable)) {
            ((RendererController.ISelfUpdateRenderable) iRenderable).triggerUpdate();
        }
    }
}
