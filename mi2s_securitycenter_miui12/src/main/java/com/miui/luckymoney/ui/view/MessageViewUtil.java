package com.miui.luckymoney.ui.view;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import b.b.c.j.i;
import com.miui.networkassistant.config.Constants;

public class MessageViewUtil {
    private MessageViewUtil() {
    }

    public static void removeMessageView(View view) {
        Context context = view.getContext();
        if (view.getParent() != null) {
            ((WindowManager) context.getSystemService("window")).removeView(view);
        }
    }

    public static void showMessageView(View view, int i, int i2, int i3) {
        Context context = view.getContext();
        WindowManager windowManager = (WindowManager) context.getSystemService("window");
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (i3 == 0) {
            i3 = 2010;
        }
        layoutParams.type = i3;
        layoutParams.format = -3;
        layoutParams.flags = 1312;
        layoutParams.gravity = 49;
        layoutParams.width = i;
        layoutParams.height = i2;
        if (i.e()) {
            int identifier = context.getResources().getIdentifier("status_bar_height", "dimen", Constants.System.ANDROID_PACKAGE_NAME);
            layoutParams.y = identifier > 0 ? context.getResources().getDimensionPixelSize(identifier) : 0;
        }
        if (view.getParent() != null) {
            windowManager.removeView(view);
        }
        windowManager.addView(view, layoutParams);
    }

    public static void showMoneyMessageView(View view, int i, int i2, int i3) {
        WindowManager windowManager = (WindowManager) view.getContext().getSystemService("window");
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (i3 == 0) {
            i3 = 2010;
        }
        layoutParams.type = i3;
        layoutParams.format = -3;
        layoutParams.flags = 1312;
        layoutParams.gravity = 49;
        layoutParams.width = i;
        layoutParams.height = i2;
        if (view.getParent() != null) {
            windowManager.removeView(view);
        }
        windowManager.addView(view, layoutParams);
    }
}
