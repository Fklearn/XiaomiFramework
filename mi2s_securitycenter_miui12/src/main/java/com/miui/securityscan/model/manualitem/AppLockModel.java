package com.miui.securityscan.model.manualitem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.miui.applicationlock.c.C0259c;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;

public class AppLockModel extends AbsModel {
    public AppLockModel(String str, Integer num) {
        super(str, num);
        setTrackStr("app_lock");
        setDelayOptimized(true);
    }

    public String getDesc() {
        return null;
    }

    public int getIndex() {
        return 9;
    }

    public String getSummary() {
        return getContext().getString(R.string.summary_app_lock_disable);
    }

    public String getTitle() {
        return getContext().getString(R.string.title_app_lock_disable);
    }

    public void optimize(Context context) {
        if (context instanceof Activity) {
            Intent intent = new Intent("com.miui.securitycenter.action.TRANSITION").setPackage(getContext().getPackageName());
            intent.putExtra("enter_way", "00007");
            ((Activity) context).startActivityForResult(intent, 100);
        }
    }

    public void scan() {
        setSafe(C0259c.b(getContext()).d() ? AbsModel.State.SAFE : AbsModel.State.DANGER);
    }
}
