package com.miui.firstaidkit.model.other;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import b.b.c.j.A;
import b.b.c.j.x;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import java.io.File;
import miui.os.Build;

public class SystemRootModel extends AbsModel {
    private static final String CLASS_NAME_UPDATER = "com.android.updater.MainActivity";
    private static final String PKG_UPDATER = "com.android.updater";
    private static final String TAG = "SystemRootModel";
    private static final String USER_ACTION = "user_action";
    private static final String USER_ACTION_UPDATE_FULL = "user_action_update_full";

    public SystemRootModel(String str, Integer num) {
        super(str, num);
        setTrackStr("system_root");
        setDelayOptimized(true);
    }

    private boolean checkSystemRoot() {
        try {
            String[] strArr = {"/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/"};
            for (int i = 0; i < strArr.length; i++) {
                if (new File(strArr[i] + "su").exists()) {
                    return true;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "checkSystemRoot : ", e);
        }
        return false;
    }

    public String getButtonTitle() {
        return getContext().getString(R.string.first_aid_card_system_root_button);
    }

    public String getDesc() {
        return "";
    }

    public int getIndex() {
        return 48;
    }

    public String getSummary() {
        return getContext().getString(R.string.first_aid_card_system_root_summary);
    }

    public String getTitle() {
        return getContext().getString(R.string.first_aid_card_system_root_title);
    }

    public void optimize(Context context) {
        Intent intent = new Intent();
        intent.setClassName(PKG_UPDATER, CLASS_NAME_UPDATER);
        intent.putExtra(USER_ACTION, USER_ACTION_UPDATE_FULL);
        if (!x.a(context, intent, 100)) {
            A.a(context, (int) R.string.app_not_installed_toast);
        }
    }

    public void scan() {
        boolean z = true;
        if (!(!Build.IS_INTERNATIONAL_BUILD && !Build.IS_ALPHA_BUILD) || !checkSystemRoot()) {
            z = false;
        }
        setSafe(!z ? AbsModel.State.SAFE : AbsModel.State.DANGER);
    }
}
