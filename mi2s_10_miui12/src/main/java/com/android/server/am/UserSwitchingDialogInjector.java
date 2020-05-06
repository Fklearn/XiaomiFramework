package com.android.server.am;

import android.app.Dialog;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;

public class UserSwitchingDialogInjector {
    private static ViewTreeObserver.OnWindowShownListener onWindowShownListener;

    public static void switchUser(Handler handler, final BaseUserSwitchingDialog dialog) {
        onWindowShownListener = new ViewTreeObserver.OnWindowShownListener() {
            public void onWindowShown() {
                BaseUserSwitchingDialog.this.startUser();
            }
        };
        View view = dialog.getWindow().getDecorView();
        if (view != null) {
            view.getViewTreeObserver().addOnWindowShownListener(onWindowShownListener);
        }
    }

    public static void finishSwitchUser(Dialog dialog) {
        View view = dialog.getWindow().getDecorView();
        if (view != null) {
            view.getViewTreeObserver().removeOnWindowShownListener(onWindowShownListener);
        }
    }

    public static void startUserInForeground(ActivityManagerService ams, int userId, Dialog dialog) {
        ams.mUserController.startUserInForeground(userId);
    }
}
