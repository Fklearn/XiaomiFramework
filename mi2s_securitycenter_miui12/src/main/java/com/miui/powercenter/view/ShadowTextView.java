package com.miui.powercenter.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.miui.securitycenter.R;

public class ShadowTextView extends RelativeLayout {

    /* renamed from: a  reason: collision with root package name */
    private TextView f7356a;

    /* renamed from: b  reason: collision with root package name */
    private Paint f7357b;

    public ShadowTextView(Context context) {
        this(context, (AttributeSet) null);
    }

    public ShadowTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ShadowTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setPadding(20, 0, 20, 40);
        setGravity(17);
        setLayerType(1, (Paint) null);
        this.f7357b = new Paint();
        this.f7357b.setColor(-1);
        this.f7357b.setStyle(Paint.Style.FILL);
        this.f7357b.setAntiAlias(true);
        this.f7357b.setShadowLayer(30.0f, 0.0f, 15.0f, Color.parseColor("#190099FF"));
    }

    public void a(Typeface typeface, int i) {
        TextView textView = this.f7356a;
        if (textView != null) {
            textView.setTypeface(typeface, i);
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        TextView textView = this.f7356a;
        if (textView != null) {
            canvas.drawRoundRect(new RectF(textView.getX(), this.f7356a.getY(), this.f7356a.getX() + ((float) this.f7356a.getWidth()), this.f7356a.getY() + ((float) this.f7356a.getHeight())), 40.0f, 40.0f, this.f7357b);
            canvas.save();
            super.dispatchDraw(canvas);
        }
    }

    public int getPaddingBottom() {
        return 40;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.f7356a = (TextView) findViewById(R.id.text);
        if (this.f7356a == null) {
            throw new IllegalArgumentException("There has no TextView with id \"text\" inflated.");
        }
    }

    public void setText(CharSequence charSequence) {
        TextView textView = this.f7356a;
        if (textView != null) {
            textView.setText(charSequence);
        }
    }
}
