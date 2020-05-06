package com.miui.gamebooster.m;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.Window;

public class fa {
    public static void a(@NonNull Activity activity) {
        Window window = activity.getWindow();
        if (window != null) {
            window.getDecorView().setOnSystemUiVisibilityChangeListener(new ea(activity));
        }
    }
}
