package com.miui.maml.elements;

import android.graphics.Canvas;
import android.util.Log;
import com.miui.maml.CommandTrigger;
import com.miui.maml.CommandTriggers;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.Variables;
import org.w3c.dom.Element;

public class CanvasDrawerElement extends AnimatedScreenElement {
    public static final String TAG_NAME = "CanvasDrawer";
    private IndexedVariable mCanvasVar;
    private CommandTrigger mDrawCommands;
    private IndexedVariable mHVar;
    private IndexedVariable mWVar;
    private IndexedVariable mXVar;
    private IndexedVariable mYVar;

    public CanvasDrawerElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        CommandTriggers commandTriggers = this.mTriggers;
        if (commandTriggers != null) {
            this.mDrawCommands = commandTriggers.find("draw");
        }
        if (this.mDrawCommands == null) {
            Log.e(TAG_NAME, "no draw commands.");
        }
        Variables variables = getVariables();
        this.mXVar = new IndexedVariable("__x", variables, true);
        this.mYVar = new IndexedVariable("__y", variables, true);
        this.mWVar = new IndexedVariable("__w", variables, true);
        this.mHVar = new IndexedVariable("__h", variables, true);
        this.mCanvasVar = new IndexedVariable("__objCanvas", getVariables(), false);
    }

    /* access modifiers changed from: protected */
    public void doRender(Canvas canvas) {
        if (this.mDrawCommands != null) {
            float widthRaw = getWidthRaw();
            float heightRaw = getHeightRaw();
            float left = getLeft(0.0f, widthRaw);
            float top = getTop(0.0f, heightRaw);
            this.mXVar.set((double) left);
            this.mYVar.set((double) top);
            this.mWVar.set((double) widthRaw);
            this.mHVar.set((double) heightRaw);
            this.mCanvasVar.set((Object) canvas);
            this.mDrawCommands.perform();
            this.mCanvasVar.set((Object) null);
        }
    }
}
