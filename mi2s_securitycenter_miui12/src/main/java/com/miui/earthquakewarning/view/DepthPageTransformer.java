package com.miui.earthquakewarning.view;

import android.support.v4.view.ViewPager;
import android.view.View;

public class DepthPageTransformer implements ViewPager.PageTransformer {
    public void transformPage(View view, float f) {
        int width = view.getWidth();
        if (f >= -1.0f) {
            if (f <= 0.0f) {
                view.setAlpha(1.0f + f);
                view.setTranslationX(((float) width) * (-f));
                return;
            } else if (f <= 1.0f) {
                view.setVisibility(0);
                view.setAlpha(1.0f - f);
                view.setTranslationX(((float) width) * (-f));
                if (f == 1.0f) {
                    view.setVisibility(4);
                    return;
                }
                return;
            }
        }
        view.setAlpha(0.0f);
    }
}
