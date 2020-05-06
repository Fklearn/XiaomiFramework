package com.miui.powercenter.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageView;
import com.miui.securitycenter.R;

@TargetApi(21)
public class d extends ImageView implements Checkable {

    /* renamed from: a  reason: collision with root package name */
    private boolean f7360a;

    /* renamed from: b  reason: collision with root package name */
    private int[] f7361b;

    /* renamed from: c  reason: collision with root package name */
    private float f7362c;

    /* renamed from: d  reason: collision with root package name */
    private Drawable f7363d;
    private Drawable e;

    public d(Context context) {
        this(context, (AttributeSet) null);
    }

    public d(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public d(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f7362c = (float) getResources().getDimensionPixelSize(R.dimen.pc_power_history_button_corner);
        float f = this.f7362c;
        RoundRectShape roundRectShape = new RoundRectShape(new float[]{f, f, f, f, f, f, f, f}, (RectF) null, (float[]) null);
        this.f7363d = new ShapeDrawable(roundRectShape);
        this.f7363d.setTint(-1);
        this.e = new ShapeDrawable(roundRectShape);
        this.e.setTint(context.getResources().getColor(R.color.pc_battery_statics_chart_float_text_background));
    }

    public float getCorner() {
        return this.f7362c;
    }

    public boolean isChecked() {
        return this.f7360a;
    }

    public void setChecked(boolean z) {
        this.f7360a = z;
        setBackground(z ? this.e : this.f7363d);
        setImageResource(z ? this.f7361b[0] : this.f7361b[1]);
    }

    public void setCorner(float f) {
        this.f7362c = f;
    }

    public void setImageResources(int[] iArr) {
        this.f7361b = iArr;
    }

    public void toggle() {
        setChecked(!this.f7360a);
    }
}
