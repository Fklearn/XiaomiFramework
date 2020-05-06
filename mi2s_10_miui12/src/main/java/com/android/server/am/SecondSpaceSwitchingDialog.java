package com.android.server.am;

import android.content.Context;
import android.miui.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

class SecondSpaceSwitchingDialog extends BaseUserSwitchingDialog {
    public SecondSpaceSwitchingDialog(ActivityManagerService service, Context context, int userId) {
        super(service, context, R.style.SecondSpaceSwitchDialog, userId);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window win = getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = -1;
        lp.height = -1;
        win.setAttributes(lp);
        win.getDecorView().setSystemUiVisibility(3846);
        setContentView(LayoutInflater.from(getContext()).inflate(R.layout.user_switching_dialog, (ViewGroup) null));
    }
}
