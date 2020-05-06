package com.miui.powercenter.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Outline;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import com.miui.securitycenter.i;

@TargetApi(21)
public class CornerImageView extends ImageView {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public float f7326a;

    private class a extends ViewOutlineProvider {
        private a() {
        }

        public void getOutline(View view, Outline outline) {
            outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), CornerImageView.this.f7326a);
        }
    }

    public CornerImageView(Context context) {
        this(context, (AttributeSet) null);
    }

    public CornerImageView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CornerImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        a(context, attributeSet, i);
    }

    private void a() {
        if (this.f7326a >= 0.0f) {
            setClipToOutline(true);
            setOutlineProvider(new a());
        }
    }

    private void a(Context context, AttributeSet attributeSet, int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, i.CornerImageView, i, 0);
        this.f7326a = (float) obtainStyledAttributes.getDimensionPixelSize(0, 0);
        obtainStyledAttributes.recycle();
        a();
    }
}
