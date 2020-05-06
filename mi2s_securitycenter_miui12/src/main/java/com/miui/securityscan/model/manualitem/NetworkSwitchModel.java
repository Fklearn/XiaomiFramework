package com.miui.securityscan.model.manualitem;

import android.content.Context;
import com.miui.securitycenter.R;
import com.miui.securitycenter.h;
import com.miui.securityscan.model.AbsModel;
import miui.os.Build;

public class NetworkSwitchModel extends AbsModel {
    public NetworkSwitchModel(String str, Integer num) {
        super(str, num);
        setTrackStr("network_switch");
    }

    public String getDesc() {
        return null;
    }

    public int getIndex() {
        return 4;
    }

    public String getSummary() {
        return getContext().getString(R.string.summary_network_switch);
    }

    public String getTitle() {
        return getContext().getString(R.string.title_network_switch);
    }

    public void optimize(Context context) {
        h.b(true);
        setSafe(AbsModel.State.SAFE);
        runOnUiThread(new f(this, context));
    }

    public void scan() {
        setSafe((!Build.IS_INTERNATIONAL_BUILD && !h.i()) ? AbsModel.State.DANGER : AbsModel.State.SAFE);
    }
}
