package com.android.server.am;

import android.content.Context;
import android.miui.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class KidSpaceSwitchingDialog extends BaseUserSwitchingDialog {
    public /* bridge */ /* synthetic */ void show() {
        super.show();
    }

    public KidSpaceSwitchingDialog(ActivityManagerService service, Context context, int userId) {
        super(service, context, R.style.KidSpaceSwitchDialog, userId);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window win = getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = -1;
        lp.height = -1;
        lp.screenOrientation = 1;
        win.setAttributes(lp);
        win.getDecorView().setSystemUiVisibility(3846);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.kid_user_switching_dialog, (ViewGroup) null);
        ((ImageView) view.findViewById(R.id.kid_switch_dialog_image)).setBackgroundResource(getKidSwitchDialogImageId());
        setContentView(view);
    }

    private int getKidSwitchDialogImageId() {
        if (this.mUserId == 0) {
            return R.drawable.exit_kid_space;
        }
        return R.drawable.enter_kid_space;
    }
}
