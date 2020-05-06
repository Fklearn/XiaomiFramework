package com.miui.maml.elements;

import android.graphics.Canvas;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewConfiguration;
import com.miui.maml.ScreenElementRoot;
import org.w3c.dom.Element;

public class ButtonScreenElement extends ElementGroup {
    private static final String LOG_TAG = "ButtonScreenElement";
    public static final String TAG_NAME = "Button";
    private boolean mIsAlignChildren;
    private ButtonActionListener mListener;
    private String mListenerName;
    private ElementGroup mNormalElements;
    private ElementGroup mPressedElements;
    private float mPreviousTapPositionX;
    private float mPreviousTapPositionY;
    private long mPreviousTapUpTime;

    public interface ButtonActionListener {
        boolean onButtonDoubleClick(String str);

        boolean onButtonDown(String str);

        boolean onButtonLongClick(String str);

        boolean onButtonUp(String str);
    }

    public ButtonScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        load(element);
    }

    private void load(Element element) {
        if (element != null) {
            this.mIsAlignChildren = Boolean.parseBoolean(getAttr(element, "alignChildren"));
            this.mListenerName = getAttr(element, "listener");
            this.mTouchable = true;
        }
    }

    private void showNormalElements() {
        ElementGroup elementGroup = this.mNormalElements;
        if (elementGroup != null) {
            elementGroup.show(true);
        }
        ElementGroup elementGroup2 = this.mPressedElements;
        if (elementGroup2 != null) {
            elementGroup2.show(false);
        }
    }

    private void showPressedElements() {
        ElementGroup elementGroup;
        ElementGroup elementGroup2 = this.mPressedElements;
        boolean z = true;
        if (elementGroup2 != null) {
            elementGroup2.show(true);
            elementGroup = this.mNormalElements;
            if (elementGroup != null) {
                z = false;
            } else {
                return;
            }
        } else {
            elementGroup = this.mNormalElements;
            if (elementGroup == null) {
                return;
            }
        }
        elementGroup.show(z);
    }

    /* access modifiers changed from: protected */
    public void doRender(Canvas canvas) {
        canvas.save();
        if (!this.mIsAlignChildren) {
            canvas.translate(-getLeft(), -getTop());
        }
        super.doRender(canvas);
        canvas.restore();
    }

    /* access modifiers changed from: protected */
    public float getParentLeft() {
        float f = 0.0f;
        float left = this.mIsAlignChildren ? getLeft() : 0.0f;
        ElementGroup elementGroup = this.mParent;
        if (elementGroup != null) {
            f = elementGroup.getParentLeft();
        }
        return left + f;
    }

    /* access modifiers changed from: protected */
    public float getParentTop() {
        float f = 0.0f;
        float top = this.mIsAlignChildren ? getTop() : 0.0f;
        ElementGroup elementGroup = this.mParent;
        if (elementGroup != null) {
            f = elementGroup.getParentTop();
        }
        return top + f;
    }

    public void init() {
        super.init();
        if (this.mListener == null && !TextUtils.isEmpty(this.mListenerName)) {
            try {
                this.mListener = (ButtonActionListener) this.mRoot.findElement(this.mListenerName);
            } catch (ClassCastException unused) {
                Log.e(LOG_TAG, "button listener designated by the name is not actually a listener: " + this.mListenerName);
            }
        }
        showNormalElements();
    }

    /* access modifiers changed from: protected */
    public void onActionCancel() {
        super.onActionCancel();
        resetState();
    }

    /* access modifiers changed from: protected */
    public void onActionDown(float f, float f2) {
        super.onActionDown(f, f2);
        ButtonActionListener buttonActionListener = this.mListener;
        if (buttonActionListener != null) {
            buttonActionListener.onButtonDown(this.mName);
        }
        if (SystemClock.uptimeMillis() - this.mPreviousTapUpTime <= ((long) ViewConfiguration.getDoubleTapTimeout())) {
            float f3 = f - this.mPreviousTapPositionX;
            float f4 = f2 - this.mPreviousTapPositionY;
            float f5 = (f3 * f3) + (f4 * f4);
            int scaledDoubleTapSlop = ViewConfiguration.get(getContext().mContext).getScaledDoubleTapSlop();
            if (f5 < ((float) (scaledDoubleTapSlop * scaledDoubleTapSlop))) {
                ButtonActionListener buttonActionListener2 = this.mListener;
                if (buttonActionListener2 != null) {
                    buttonActionListener2.onButtonDoubleClick(this.mName);
                }
                performAction("double");
            }
        }
        this.mPreviousTapPositionX = f;
        this.mPreviousTapPositionY = f2;
        showPressedElements();
        ElementGroup elementGroup = this.mPressedElements;
        if (elementGroup != null) {
            elementGroup.reset();
        }
    }

    public void onActionUp() {
        super.onActionUp();
        ButtonActionListener buttonActionListener = this.mListener;
        if (buttonActionListener != null) {
            buttonActionListener.onButtonUp(this.mName);
        }
        this.mPreviousTapUpTime = SystemClock.uptimeMillis();
        resetState();
    }

    /* access modifiers changed from: protected */
    public ScreenElement onCreateChild(Element element) {
        String tagName = element.getTagName();
        if (tagName.equalsIgnoreCase("Normal")) {
            ElementGroup elementGroup = new ElementGroup(element, this.mRoot);
            this.mNormalElements = elementGroup;
            return elementGroup;
        } else if (!tagName.equalsIgnoreCase("Pressed")) {
            return super.onCreateChild(element);
        } else {
            ElementGroup elementGroup2 = new ElementGroup(element, this.mRoot);
            this.mPressedElements = elementGroup2;
            return elementGroup2;
        }
    }

    /* access modifiers changed from: protected */
    public void resetState() {
        showNormalElements();
        ElementGroup elementGroup = this.mNormalElements;
        if (elementGroup != null) {
            elementGroup.reset();
        }
    }

    public void setListener(ButtonActionListener buttonActionListener) {
        this.mListener = buttonActionListener;
    }
}
