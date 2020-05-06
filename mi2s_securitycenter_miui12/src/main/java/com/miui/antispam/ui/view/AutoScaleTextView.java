package com.miui.antispam.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

public class AutoScaleTextView extends TextView {
    public AutoScaleTextView(Context context) {
        this(context, (AttributeSet) null);
    }

    public AutoScaleTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AutoScaleTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setSingleLine();
    }

    private void a(String str) {
        int width = (getWidth() - getPaddingStart()) - getPaddingEnd();
        if (width > 0) {
            TextPaint textPaint = new TextPaint(getPaint());
            float textSize = textPaint.getTextSize();
            while (textPaint.measureText(str) > ((float) width)) {
                float f = textSize - 1.0f;
                if (f <= 0.0f) {
                    break;
                }
                textPaint.setTextSize(f);
                textSize = f;
            }
            setTextSize(0, textSize);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        a(getText().toString());
        super.onDraw(canvas);
    }
}
