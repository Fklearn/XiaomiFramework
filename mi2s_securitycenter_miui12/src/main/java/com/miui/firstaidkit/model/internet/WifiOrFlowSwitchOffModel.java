package com.miui.firstaidkit.model.internet;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import b.b.c.j.A;
import b.b.c.j.x;
import com.miui.securitycenter.R;
import com.miui.securityscan.i.c;
import com.miui.securityscan.model.AbsModel;

public class WifiOrFlowSwitchOffModel extends AbsModel {
    private final ContentResolver mResolver = getContext().getContentResolver();

    public WifiOrFlowSwitchOffModel(String str, Integer num) {
        super(str, num);
        setDelayOptimized(true);
        setTrackStr("wifi_or_flow");
        setTrackIgnoreStr(getTrackStr() + "_ignore");
    }

    public String getButtonTitle() {
        return getContext().getString(R.string.first_aid_card_wifi_or_flow_button);
    }

    public String getDesc() {
        return "";
    }

    public int getIndex() {
        return 52;
    }

    public String getSummary() {
        return getContext().getString(R.string.first_aid_card_wifi_or_flow_summary);
    }

    public String getTitle() {
        return getContext().getString(R.string.first_aid_card_wifi_or_flow_title);
    }

    public void optimize(Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.MainSettings"));
        if (!x.a(context, intent, 100)) {
            A.a(context, (int) R.string.app_not_installed_toast);
        }
    }

    public void scan() {
        boolean z = false;
        if (Settings.Global.getInt(this.mResolver, "airplane_mode_on", 0) != 0) {
            z = true;
        }
        if (!z) {
            setSafe(c.f(getContext()) ? AbsModel.State.SAFE : AbsModel.State.DANGER);
        }
    }
}
