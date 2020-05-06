package com.miui.securityscan.ui.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import com.miui.securitycenter.R;

public class FlowRankLineView extends View {

    /* renamed from: a  reason: collision with root package name */
    private Paint f7991a;

    /* renamed from: b  reason: collision with root package name */
    private Rect f7992b;

    public FlowRankLineView(Context context) {
        super(context);
        a();
    }

    public FlowRankLineView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        a();
    }

    public FlowRankLineView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        a();
    }

    private void a() {
        this.f7991a = new Paint();
        this.f7991a.setColor(getResources().getColor(R.color.flow_rank_line_coordinate_color));
        this.f7991a.setStyle(Paint.Style.STROKE);
        this.f7991a.setAntiAlias(true);
        this.f7991a.setPathEffect(new DashPathEffect(new float[]{3.0f, 3.0f}, 0.0f));
        this.f7992b = new Rect();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), this.f7991a);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        Rect rect = this.f7992b;
        rect.left = i;
        rect.top = i2;
        rect.right = i3;
        rect.bottom = i4;
    }
}
