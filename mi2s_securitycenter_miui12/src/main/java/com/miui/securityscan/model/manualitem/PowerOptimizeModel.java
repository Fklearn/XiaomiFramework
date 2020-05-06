package com.miui.securityscan.model.manualitem;

import android.content.Context;
import android.content.Intent;
import b.b.c.j.A;
import b.b.c.j.x;
import com.miui.powercenter.y;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;

public class PowerOptimizeModel extends AbsModel {
    public PowerOptimizeModel(String str, Integer num) {
        super(str, num);
        setDelayOptimized(true);
        setTrackStr("power_optimizer");
    }

    public String getDesc() {
        return null;
    }

    public int getIndex() {
        return 12;
    }

    public String getSummary() {
        return getContext().getString(R.string.summary_power_optimizer);
    }

    public String getTitle() {
        return getContext().getString(R.string.title_power_optimizer);
    }

    public void optimize(Context context) {
        if (!x.a(context, new Intent("com.miui.powercenter.PowerAutoSave"), 100)) {
            A.a(context, (int) R.string.app_not_installed_toast);
        }
    }

    public void scan() {
        setSafe(y.k() ? AbsModel.State.SAFE : AbsModel.State.DANGER);
    }
}
