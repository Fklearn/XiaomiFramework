package com.miui.gamebooster.videobox.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.miui.securitycenter.R;

public class ClipFrameLayout extends FrameLayout {

    /* renamed from: a  reason: collision with root package name */
    private int f5209a;

    /* renamed from: b  reason: collision with root package name */
    private int f5210b;

    public ClipFrameLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public ClipFrameLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ClipFrameLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f5209a = context.getResources().getDimensionPixelSize(R.dimen.videobox_main_ps);
        this.f5210b = context.getResources().getDimensionPixelSize(R.dimen.vtb_main_settings_me);
    }

    public static boolean a(View view) {
        return view.getLayoutDirection() == 1;
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        int i;
        int i2;
        int i3;
        canvas.save();
        if (a(this)) {
            i3 = this.f5210b;
            i2 = getWidth();
            i = this.f5209a;
        } else {
            i3 = this.f5209a;
            i2 = getWidth();
            i = this.f5210b;
        }
        canvas.clipRect(i3, 0, i2 - i, getHeight());
        super.dispatchDraw(canvas);
        canvas.restore();
    }
}
