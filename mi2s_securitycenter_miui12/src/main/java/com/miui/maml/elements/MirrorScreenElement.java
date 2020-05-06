package com.miui.maml.elements;

import android.graphics.Canvas;
import android.util.Log;
import com.miui.maml.ScreenElementRoot;
import org.w3c.dom.Element;

public class MirrorScreenElement extends AnimatedScreenElement {
    private static final String LOG_TAG = "MirrorScreenElement";
    public static final String TAG_NAME = "Mirror";
    private boolean mMirrorTranslation;
    private ScreenElement mTarget;
    private String mTargetName;

    public MirrorScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mTargetName = element.getAttribute("target");
        this.mMirrorTranslation = Boolean.parseBoolean(element.getAttribute("mirrorTranslation"));
    }

    /* access modifiers changed from: protected */
    public void doRender(Canvas canvas) {
        ScreenElement screenElement = this.mTarget;
        if (screenElement == null) {
            return;
        }
        if (!this.mMirrorTranslation || !(screenElement instanceof AnimatedScreenElement)) {
            this.mTarget.doRender(canvas);
        } else {
            ((AnimatedScreenElement) screenElement).doRenderWithTranslation(canvas);
        }
    }

    public void init() {
        super.init();
        this.mTarget = this.mRoot.findElement(this.mTargetName);
        if (this.mTarget == null) {
            Log.e(LOG_TAG, "the target does not exist: " + this.mTargetName);
        }
    }
}
