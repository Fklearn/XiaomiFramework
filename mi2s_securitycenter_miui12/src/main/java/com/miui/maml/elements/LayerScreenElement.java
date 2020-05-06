package com.miui.maml.elements;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import com.miui.maml.ScreenElementRoot;
import org.w3c.dom.Element;

public class LayerScreenElement extends ViewHolderScreenElement {
    public static final String TAG_NAME = "Layer";
    private LayerView mView;

    private class LayerView extends View {
        public LayerView(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            LayerScreenElement.this.doRender(canvas);
            LayerScreenElement.this.mController.doneRender();
        }
    }

    public LayerScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mView = new LayerView(screenElementRoot.getContext().mContext);
    }

    /* access modifiers changed from: protected */
    public View getView() {
        return this.mView;
    }
}
