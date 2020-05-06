package com.miui.maml.elements;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.WindowManager;
import com.miui.maml.RendererController;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.animation.BaseAnimation;
import java.util.ArrayList;
import org.w3c.dom.Element;

public abstract class ViewHolderScreenElement extends ElementGroupRC {
    private static final String LOG_TAG = "MAML ViewHolderScreenElement";
    /* access modifiers changed from: private */
    public boolean mHardware;
    /* access modifiers changed from: private */
    public ViewGroup.LayoutParams mLayoutParams = getLayoutParam();
    protected boolean mUpdatePosition;
    protected boolean mUpdateSize;
    protected boolean mUpdateTranslation;
    /* access modifiers changed from: private */
    public boolean mViewAdded;

    private class ProxyListener extends RendererController.EmptyListener {
        private ProxyListener() {
        }

        public void doRender() {
            ViewHolderScreenElement.this.getView().postInvalidate();
        }

        public void forceUpdate() {
            ViewHolderScreenElement.this.mRoot.getRendererController().forceUpdate();
        }

        public void tick(long j) {
            ViewHolderScreenElement.this.doTickChildren(j);
        }

        public void triggerUpdate() {
            ViewHolderScreenElement.this.mRoot.getRendererController().triggerUpdate();
        }
    }

    public ViewHolderScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mHardware = Boolean.parseBoolean(getAttr(element, "hardware"));
        this.mUpdatePosition = getAttrAsBoolean(getAttr(element, "updatePosition"), true);
        this.mUpdateSize = getAttrAsBoolean(getAttr(element, "updateSize"), true);
        this.mUpdateTranslation = getAttrAsBoolean(getAttr(element, "updateTranslation"), true);
    }

    private final void finishView() {
        if (this.mViewAdded && this.mRoot.getViewManager() != null) {
            postInMainThread(new Runnable() {
                public void run() {
                    ViewManager viewManager = ViewHolderScreenElement.this.mRoot.getViewManager();
                    if (viewManager != null) {
                        View view = ViewHolderScreenElement.this.getView();
                        viewManager.removeView(view);
                        boolean unused = ViewHolderScreenElement.this.mViewAdded = false;
                        ViewHolderScreenElement.this.onViewRemoved(view);
                    }
                }
            });
        }
    }

    private static boolean getAttrAsBoolean(String str, boolean z) {
        return TextUtils.isEmpty(str) ? z : Boolean.parseBoolean(str);
    }

    private final void initView() {
        if (!this.mViewAdded) {
            postInMainThread(new Runnable() {
                public void run() {
                    ViewManager viewManager = ViewHolderScreenElement.this.mRoot.getViewManager();
                    if (!ViewHolderScreenElement.this.mViewAdded && viewManager != null) {
                        View view = ViewHolderScreenElement.this.getView();
                        ViewHolderScreenElement.this.onUpdateView(view);
                        viewManager.addView(view, ViewHolderScreenElement.this.mLayoutParams);
                        if (ViewHolderScreenElement.this.mHardware) {
                            view.setLayerType(2, (Paint) null);
                        }
                        boolean unused = ViewHolderScreenElement.this.mViewAdded = true;
                        ViewHolderScreenElement.this.onViewAdded(view);
                    }
                }
            });
        }
    }

    private boolean updateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        boolean z;
        int width = (int) getWidth();
        if (layoutParams.width != width) {
            layoutParams.width = width;
            z = true;
        } else {
            z = false;
        }
        int height = (int) getHeight();
        if (layoutParams.height == height) {
            return z;
        }
        layoutParams.height = height;
        return true;
    }

    /* access modifiers changed from: protected */
    public void doTick(long j) {
        if (this.mController == null) {
            super.doTick(j);
            getView().postInvalidate();
        } else {
            doTickSelf(j);
        }
        udpateView();
    }

    /* access modifiers changed from: protected */
    public void doTickSelf(long j) {
        ArrayList<BaseAnimation> arrayList = this.mAnimations;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                this.mAnimations.get(i).tick(j);
            }
        }
        this.mAlpha = evaluateAlpha();
        int i2 = this.mAlpha;
        if (i2 < 0) {
            i2 = 0;
        }
        this.mAlpha = i2;
    }

    public void finish() {
        super.finish();
        finishView();
    }

    /* access modifiers changed from: protected */
    public ViewGroup.LayoutParams getLayoutParam() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-1, -1);
        layoutParams.format = 1;
        layoutParams.flags = 256;
        return layoutParams;
    }

    /* access modifiers changed from: protected */
    public abstract View getView();

    public void init() {
        super.init();
        if (this.mRoot.getViewManager() != null) {
            initView();
        } else {
            Log.e(LOG_TAG, "ViewManager must be set before init");
        }
    }

    /* access modifiers changed from: protected */
    public boolean isViewAdded() {
        return this.mViewAdded;
    }

    /* access modifiers changed from: protected */
    public void onControllerCreated(RendererController rendererController) {
        rendererController.setListener(new ProxyListener());
    }

    /* access modifiers changed from: protected */
    public void onUpdateView(View view) {
        if (this.mUpdatePosition) {
            view.setX(getAbsoluteLeft());
            view.setY(getAbsoluteTop());
        }
        if (this.mUpdateTranslation) {
            view.setPivotX(getPivotX());
            view.setPivotY(getPivotY());
            view.setRotation(getRotation());
            view.setRotationX(getRotationX());
            view.setRotationY(getRotationY());
            view.setAlpha(((float) getAlpha()) / 255.0f);
            view.setScaleX(getScaleX());
            view.setScaleY(getScaleY());
        }
        if (this.mUpdateSize && updateLayoutParams(this.mLayoutParams)) {
            view.setLayoutParams(this.mLayoutParams);
        }
    }

    /* access modifiers changed from: protected */
    public void onViewAdded(View view) {
    }

    /* access modifiers changed from: protected */
    public void onViewRemoved(View view) {
    }

    /* access modifiers changed from: protected */
    public void onVisibilityChange(final boolean z) {
        postInMainThread(new Runnable() {
            public void run() {
                ViewHolderScreenElement.this.getView().setVisibility(z ? 0 : 4);
            }
        });
    }

    public void render(Canvas canvas) {
    }

    public void setHardwareLayer(final boolean z) {
        postInMainThread(new Runnable() {
            public void run() {
                ViewHolderScreenElement.this.getView().setLayerType(z ? 2 : 0, (Paint) null);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void udpateView() {
        if (this.mUpdatePosition || this.mUpdateTranslation || this.mUpdateSize) {
            postInMainThread(new Runnable() {
                public void run() {
                    if (ViewHolderScreenElement.this.mViewAdded) {
                        ViewHolderScreenElement.this.onUpdateView(ViewHolderScreenElement.this.getView());
                    }
                }
            });
        }
    }
}
