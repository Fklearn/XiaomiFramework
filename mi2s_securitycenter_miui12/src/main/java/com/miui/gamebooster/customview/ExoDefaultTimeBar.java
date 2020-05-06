package com.miui.gamebooster.customview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.google.android.exoplayer2.ui.DefaultTimeBar;

public class ExoDefaultTimeBar extends DefaultTimeBar {

    /* renamed from: a  reason: collision with root package name */
    private boolean f4122a = true;

    public ExoDefaultTimeBar(@NonNull Context context) {
        super(context, (AttributeSet) null);
        new DefaultTimeBar(context, (AttributeSet) null);
        super.setScrubberColor(0);
    }

    public ExoDefaultTimeBar(@NonNull Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        new DefaultTimeBar(context, attributeSet);
        super.setScrubberColor(0);
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    public void setOpenSeek(boolean z) {
        this.f4122a = z;
    }
}
