package com.miui.securityscan.model.manualitem;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import b.b.c.j.A;
import b.b.c.j.x;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;

public class MiRoamingModel extends AbsModel {
    public MiRoamingModel(String str, Integer num) {
        super(str, num);
        setDelayOptimized(true);
        setTrackStr("xiaomi_roaming");
    }

    public String getDesc() {
        return null;
    }

    public int getIndex() {
        return 28;
    }

    public String getSummary() {
        return getContext().getString(R.string.summary_xiaomi_roaming);
    }

    public String getTitle() {
        return getContext().getString(R.string.title_xiaomi_roaming);
    }

    public void optimize(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.miui.virtualsim.action.OPEN_APP");
        intent.putExtra("launchfrom", "virtualsim_channel_security_check");
        intent.addFlags(268435456);
        if (!x.a(context, intent, 100)) {
            A.a(context, (int) R.string.app_not_installed_toast);
        }
    }

    public void scan() {
        boolean z = false;
        try {
            Bundle call = getContext().getContentResolver().call(Uri.parse("content://com.miui.virtualsim.provider.virtualsimInfo"), "isNeedRegister", (String) null, (Bundle) null);
            if (call != null) {
                z = call.getBoolean("isNeedRegister");
            }
        } catch (Exception unused) {
        }
        setSafe(z ? AbsModel.State.DANGER : AbsModel.State.SAFE);
    }
}
