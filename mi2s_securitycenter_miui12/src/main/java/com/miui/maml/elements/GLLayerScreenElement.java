package com.miui.maml.elements;

import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import com.miui.maml.CommandTrigger;
import com.miui.maml.CommandTriggers;
import com.miui.maml.RendererController;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.Variables;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import org.w3c.dom.Element;

public class GLLayerScreenElement extends ViewHolderScreenElement {
    private static final String LOG_TAG = "MAML GLLayerScreenElement";
    public static final String TAG_NAME = "GLLayer";
    /* access modifiers changed from: private */
    public IndexedVariable mCanvasVar;
    /* access modifiers changed from: private */
    public IndexedVariable mHVar;
    private WindowManager.LayoutParams mLayoutParams;
    /* access modifiers changed from: private */
    public CommandTrigger mOnSurfaceChangeCommands;
    /* access modifiers changed from: private */
    public CommandTrigger mOnSurfaceCreateCommands;
    /* access modifiers changed from: private */
    public CommandTrigger mOnSurfaceDrawCommands;
    /* access modifiers changed from: private */
    public GLSurfaceView mView;
    private IndexedVariable mViewVar;
    /* access modifiers changed from: private */
    public IndexedVariable mWVar;

    private class GLRenderer implements GLSurfaceView.Renderer {
        private GLRenderer() {
        }

        public void onDrawFrame(GL10 gl10) {
            if (GLLayerScreenElement.this.mOnSurfaceDrawCommands != null) {
                GLLayerScreenElement.this.mCanvasVar.set((Object) gl10);
                GLLayerScreenElement.this.mOnSurfaceDrawCommands.perform();
                GLLayerScreenElement.this.mCanvasVar.set((Object) null);
            }
            RendererController rendererController = GLLayerScreenElement.this.mController;
            if (rendererController != null) {
                rendererController.doneRender();
            }
        }

        public void onSurfaceChanged(GL10 gl10, int i, int i2) {
            if (GLLayerScreenElement.this.mOnSurfaceChangeCommands != null) {
                GLLayerScreenElement.this.mCanvasVar.set((Object) gl10);
                GLLayerScreenElement.this.mWVar.set((double) i);
                GLLayerScreenElement.this.mHVar.set((double) i2);
                GLLayerScreenElement.this.mOnSurfaceChangeCommands.perform();
                GLLayerScreenElement.this.mCanvasVar.set((Object) null);
            }
        }

        public void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig) {
            if (GLLayerScreenElement.this.mOnSurfaceCreateCommands != null) {
                GLLayerScreenElement.this.mCanvasVar.set((Object) gl10);
                GLLayerScreenElement.this.mOnSurfaceCreateCommands.perform();
                GLLayerScreenElement.this.mCanvasVar.set((Object) null);
            }
        }
    }

    private class ProxyListener extends RendererController.EmptyListener {
        private ProxyListener() {
        }

        public void doRender() {
            GLLayerScreenElement.this.mView.requestRender();
        }

        public void forceUpdate() {
            GLLayerScreenElement.this.mRoot.getRendererController().forceUpdate();
        }

        public void tick(long j) {
        }

        public void triggerUpdate() {
            GLLayerScreenElement.this.mRoot.getRendererController().triggerUpdate();
        }
    }

    public GLLayerScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        load(element);
    }

    private void load(Element element) {
        this.mView = new GLSurfaceView(this.mRoot.getContext().mContext);
        this.mLayoutParams = new WindowManager.LayoutParams((int) this.mRoot.getWidth(), (int) this.mRoot.getHeight());
        WindowManager.LayoutParams layoutParams = this.mLayoutParams;
        layoutParams.format = 1;
        layoutParams.flags = 256;
        this.mView.setRenderer(new GLRenderer());
        this.mView.setRenderMode(this.mController != null ? 0 : 1);
        CommandTriggers commandTriggers = this.mTriggers;
        if (commandTriggers != null) {
            this.mOnSurfaceCreateCommands = commandTriggers.find("create");
            this.mOnSurfaceChangeCommands = this.mTriggers.find("change");
            this.mOnSurfaceDrawCommands = this.mTriggers.find("draw");
        }
        if (this.mOnSurfaceDrawCommands == null) {
            Log.e("GLLayerScreenElement", "no draw commands.");
        }
        Variables variables = getVariables();
        this.mCanvasVar = new IndexedVariable("__objGLCanvas", variables, false);
        this.mViewVar = new IndexedVariable("__objGLView", variables, false);
        this.mWVar = new IndexedVariable("__w", variables, true);
        this.mHVar = new IndexedVariable("__h", variables, true);
    }

    /* access modifiers changed from: protected */
    public void doTick(long j) {
        doTickSelf(j);
        udpateView();
    }

    /* access modifiers changed from: protected */
    public View getView() {
        return this.mView;
    }

    public void init() {
        this.mViewVar.set((Object) this.mView);
        super.init();
    }

    /* access modifiers changed from: protected */
    public void onControllerCreated(RendererController rendererController) {
        rendererController.setListener(new ProxyListener());
    }
}
