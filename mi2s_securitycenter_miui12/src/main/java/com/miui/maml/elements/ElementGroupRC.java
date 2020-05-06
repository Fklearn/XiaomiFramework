package com.miui.maml.elements;

import com.miui.maml.RendererController;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.util.Utils;
import org.w3c.dom.Element;

public abstract class ElementGroupRC extends ElementGroup {
    protected RendererController mController;
    private float mFrameRate;

    public ElementGroupRC(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mFrameRate = Utils.getAttrAsFloat(element, "frameRate", 0.0f);
        if (this.mFrameRate >= 0.0f) {
            this.mController = new RendererController();
            onControllerCreated(this.mController);
            this.mController.init();
        }
    }

    public RendererController getRendererController() {
        RendererController rendererController = this.mController;
        return rendererController != null ? rendererController : super.getRendererController();
    }

    public void init() {
        super.init();
        requestFramerate(this.mFrameRate);
    }

    /* access modifiers changed from: protected */
    public abstract void onControllerCreated(RendererController rendererController);
}
