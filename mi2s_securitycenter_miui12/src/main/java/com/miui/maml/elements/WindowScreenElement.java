package com.miui.maml.elements;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import com.miui.maml.RendererController;
import com.miui.maml.ScreenElementRoot;
import org.w3c.dom.Element;

public class WindowScreenElement extends ElementGroupRC {
    public static final String TAG_NAME = "Window";
    private WindowManager.LayoutParams mLayoutParams;
    /* access modifiers changed from: private */
    public WindowView mView = new WindowView(this.mWindowContext);
    private boolean mViewAdded;
    private Context mWindowContext;
    private WindowManager mWindowManager = ((WindowManager) this.mWindowContext.getSystemService("window"));

    private class ProxyListener extends RendererController.EmptyListener {
        private ProxyListener() {
        }

        public void doRender() {
            WindowScreenElement.this.mView.postInvalidate();
        }

        public void forceUpdate() {
            WindowScreenElement.this.mRoot.getRendererController().triggerUpdate();
        }

        public void onHover(MotionEvent motionEvent) {
            WindowScreenElement.this.onHover(motionEvent);
        }

        public void onTouch(MotionEvent motionEvent) {
            WindowScreenElement.this.onTouch(motionEvent);
        }

        public void tick(long j) {
            WindowScreenElement.this.doTick(j);
        }

        public void triggerUpdate() {
            WindowScreenElement.this.mRoot.getRendererController().triggerUpdate();
        }
    }

    private class WindowView extends View {
        public WindowView(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            WindowScreenElement.this.doRenderWithTranslation(canvas);
            WindowScreenElement.this.mController.doneRender();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            WindowScreenElement.this.mController.postMessage(motionEvent);
            return super.onTouchEvent(motionEvent);
        }
    }

    public WindowScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mWindowContext = screenElementRoot.getContext().mContext;
        this.mLayoutParams = new WindowManager.LayoutParams((int) screenElementRoot.getWidth(), (int) screenElementRoot.getHeight());
        WindowManager.LayoutParams layoutParams = this.mLayoutParams;
        layoutParams.format = 1;
        layoutParams.flags = 256;
    }

    /* access modifiers changed from: private */
    public final void addView() {
        if (!this.mViewAdded) {
            this.mWindowManager.addView(this.mView, this.mLayoutParams);
            this.mViewAdded = true;
        }
    }

    /* access modifiers changed from: private */
    public final void removeView() {
        if (this.mViewAdded) {
            this.mWindowManager.removeView(this.mView);
            this.mViewAdded = false;
        }
    }

    public void init() {
        super.init();
        if (isVisible()) {
            addView();
        }
    }

    /* access modifiers changed from: protected */
    public void onControllerCreated(RendererController rendererController) {
        rendererController.setListener(new ProxyListener());
    }

    /* access modifiers changed from: protected */
    public void onVisibilityChange(final boolean z) {
        getContext().getHandler().post(new Runnable() {
            public void run() {
                if (z) {
                    WindowScreenElement.this.addView();
                } else {
                    WindowScreenElement.this.removeView();
                }
            }
        });
    }

    public void render(Canvas canvas) {
    }
}
