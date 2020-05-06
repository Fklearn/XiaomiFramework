package com.miui.superpower.statusbar.slider;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.SeekBar;

public class ToggleSeekBar extends SeekBar {

    /* renamed from: a  reason: collision with root package name */
    private String f8217a;

    /* renamed from: b  reason: collision with root package name */
    private a f8218b;

    public ToggleSeekBar(Context context) {
        this(context, (AttributeSet) null);
    }

    public ToggleSeekBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ToggleSeekBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        a();
    }

    private void a() {
        this.f8218b = new a(this, false);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        String str = this.f8217a;
        if (str != null) {
            accessibilityNodeInfo.setText(str);
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            setEnabled(true);
        }
        a aVar = this.f8218b;
        if (aVar != null) {
            aVar.a(motionEvent);
        }
        if (motionEvent.getAction() == 0) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.onTouchEvent(motionEvent);
    }

    public void setAccessibilityLabel(String str) {
        this.f8217a = str;
    }
}
