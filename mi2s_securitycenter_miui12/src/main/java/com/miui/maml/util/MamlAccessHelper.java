package com.miui.maml.util;

import a.f.a.c;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import androidx.annotation.NonNull;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.elements.AnimatedScreenElement;
import com.miui.maml.elements.ButtonScreenElement;
import java.util.List;

public class MamlAccessHelper extends c {
    private static final String TAG = "MamlAccessHelper";
    View mHostView;
    ScreenElementRoot mRoot = null;

    public MamlAccessHelper(ScreenElementRoot screenElementRoot, View view) {
        super(view);
        this.mRoot = screenElementRoot;
        this.mHostView = view;
        this.mRoot.setMamlAccessHelper(this);
    }

    /* access modifiers changed from: protected */
    public int getVirtualViewAt(float f, float f2) {
        List<AnimatedScreenElement> accessibleElements = this.mRoot.getAccessibleElements();
        for (int size = accessibleElements.size() - 1; size >= 0; size--) {
            AnimatedScreenElement animatedScreenElement = accessibleElements.get(size);
            if (animatedScreenElement.isVisible() && animatedScreenElement.touched(f, f2)) {
                return size;
            }
        }
        return Integer.MIN_VALUE;
    }

    /* access modifiers changed from: protected */
    public void getVisibleVirtualViews(List<Integer> list) {
        List<AnimatedScreenElement> accessibleElements = this.mRoot.getAccessibleElements();
        for (int i = 0; i < accessibleElements.size(); i++) {
            if (accessibleElements.get(i).isVisible()) {
                list.add(Integer.valueOf(i));
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean onPerformActionForVirtualView(int i, int i2, Bundle bundle) {
        if (i2 != 16) {
            return false;
        }
        List<AnimatedScreenElement> accessibleElements = this.mRoot.getAccessibleElements();
        if (i < 0 || i >= accessibleElements.size()) {
            return false;
        }
        AnimatedScreenElement animatedScreenElement = accessibleElements.get(i);
        animatedScreenElement.performAction("up");
        if (!(animatedScreenElement instanceof ButtonScreenElement)) {
            return true;
        }
        ((ButtonScreenElement) animatedScreenElement).onActionUp();
        return true;
    }

    /* access modifiers changed from: protected */
    public void onPopulateEventForVirtualView(int i, AccessibilityEvent accessibilityEvent) {
        List<AnimatedScreenElement> accessibleElements = this.mRoot.getAccessibleElements();
        if (i >= 0 && i < accessibleElements.size()) {
            accessibilityEvent.setContentDescription(accessibleElements.get(i).getContentDescription());
        }
    }

    /* access modifiers changed from: protected */
    public void onPopulateNodeForVirtualView(int i, @NonNull androidx.core.view.a.c cVar) {
        List<AnimatedScreenElement> accessibleElements = this.mRoot.getAccessibleElements();
        if (i < 0 || i >= accessibleElements.size()) {
            Log.e(TAG, "virtualViewId not found " + i);
            cVar.c((CharSequence) "");
            cVar.c(new Rect(0, 0, 0, 0));
            return;
        }
        AnimatedScreenElement animatedScreenElement = accessibleElements.get(i);
        String contentDescription = animatedScreenElement.getContentDescription();
        if (contentDescription == null) {
            Log.e(TAG, "element.getContentDescription() == null " + animatedScreenElement.getName());
            contentDescription = "";
        }
        cVar.c((CharSequence) contentDescription);
        cVar.a(16);
        cVar.c(new Rect((int) animatedScreenElement.getAbsoluteLeft(), (int) animatedScreenElement.getAbsoluteTop(), (int) (animatedScreenElement.getAbsoluteLeft() + animatedScreenElement.getWidth()), (int) (animatedScreenElement.getAbsoluteTop() + animatedScreenElement.getHeight())));
    }

    public void performAccessibilityAction(final int i, final int i2) {
        this.mHostView.post(new Runnable() {
            public void run() {
                MamlAccessHelper mamlAccessHelper = MamlAccessHelper.this;
                mamlAccessHelper.getAccessibilityNodeProvider(mamlAccessHelper.mHostView).a(i, i2, (Bundle) null);
            }
        });
    }
}
