package com.miui.gamebooster.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.miui.securitycenter.R;

public class CustomeLine extends View {
    public CustomeLine(Context context) {
        super(context);
    }

    public CustomeLine(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        int width = getWidth();
        int height = getHeight();
        paint.setStrokeWidth((float) height);
        paint.setColor(getResources().getColor(R.color.line_color));
        float f = (float) (height / 2);
        canvas.drawLine(0.0f, f, (float) width, f, paint);
    }
}
