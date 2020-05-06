package com.miui.securityscan.model.manualitem;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import b.b.c.j.A;
import b.b.c.j.x;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.scanner.C0569p;
import com.miui.securityscan.scanner.C0570q;
import java.io.File;
import java.util.Map;
import miui.os.Build;

public class PermissionRootModel extends AbsModel {
    private static final String CLASS_NAME_UPDATER = "com.android.updater.MainActivity";
    private static final String PKG_UPDATER = "com.android.updater";
    private static final String TAG = "PermissionRootModel";
    private static final String USER_ACTION = "user_action";
    private static final String USER_ACTION_UPDATE_FULL = "user_action_update_full";

    public PermissionRootModel(String str, Integer num) {
        super(str, num);
        setTrackStr("permission_root");
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

    public String getDesc() {
        return null;
    }

    public int getIndex() {
        return 24;
    }

    public String getSummary() {
        return getContext().getString(R.string.summary_permission_root);
    }

    public String getTitle() {
        return getContext().getString(R.string.title_permission_root);
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
        boolean z2 = !Build.IS_INTERNATIONAL_BUILD && !Build.IS_ALPHA_BUILD;
        if (!z2 || !checkSystemRoot()) {
            z = false;
        }
        setSafe(!z ? AbsModel.State.SAFE : AbsModel.State.DANGER);
        if (!z2) {
            return;
        }
        if (isSafe() == AbsModel.State.SAFE) {
            C0570q.b().a(C0570q.a.SYSTEM, getItemKey(), new C0569p(getContext().getString(R.string.title_device_not_root), false));
            return;
        }
        Map<String, C0569p> a2 = C0570q.b().a(C0570q.a.SYSTEM);
        if (a2.containsKey(getItemKey())) {
            a2.remove(getItemKey());
        }
    }
}
