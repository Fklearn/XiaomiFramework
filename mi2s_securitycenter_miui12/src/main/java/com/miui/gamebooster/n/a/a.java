package com.miui.gamebooster.n.a;

import android.view.View;
import miui.animation.Folme;
import miui.animation.base.AnimConfig;

public class a {
    public static void a(View view) {
        if (view != null) {
            try {
                Folme.useAt(new View[]{view}).touch().handleTouchOf(view, new AnimConfig[0]);
            } catch (Throwable unused) {
            }
        }
    }
}
