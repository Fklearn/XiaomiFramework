package com.miui.powercenter.autotask;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.widget.ImageView;
import miui.R;

public class FloatActionButtonView extends ImageView {
    public FloatActionButtonView(Context context) {
        this(context, (AttributeSet) null);
    }

    public FloatActionButtonView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        a();
    }

    private void a() {
        setImageDrawable(getResources().getDrawable(R.drawable.action_button_main_new_light));
        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
        shapeDrawable.getPaint().setColor(getResources().getColor(com.miui.securitycenter.R.color.pc_float_action_button));
        setBackground(shapeDrawable);
    }
}
