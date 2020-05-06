package com.miui.networkassistant.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import b.b.c.j.i;
import com.miui.securitycenter.R;

public class NotchUtil {
    private NotchUtil() {
    }

    public static void setNotchToolbarMarginTop(Context context, View view) {
        int f = i.f(context) + context.getResources().getDimensionPixelSize(R.dimen.notch_toolbar_margin_top);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) layoutParams).setMargins(0, f, 0, 0);
            view.setLayoutParams(layoutParams);
        }
    }
}
