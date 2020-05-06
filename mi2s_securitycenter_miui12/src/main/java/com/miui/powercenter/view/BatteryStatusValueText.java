package com.miui.powercenter.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.miui.networkassistant.utils.TypefaceHelper;

public class BatteryStatusValueText extends TextView {
    public BatteryStatusValueText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setTypeface(TypefaceHelper.getMiuiTypefaceForNA(context));
    }
}
