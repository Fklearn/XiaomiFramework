package com.android.server.power;

import android.view.WindowManager;

public class WindowAdapter {
    public static void setUseNotchRegion(WindowManager.LayoutParams lp) {
        lp.layoutInDisplayCutoutMode = 1;
    }
}
