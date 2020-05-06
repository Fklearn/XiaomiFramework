package com.miui.securityscan.model.manualitem;

import android.content.Context;
import com.miui.common.persistence.b;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import miui.os.Build;

public class VirusCloudScanModel extends AbsModel {
    private String cloudScanKey = Application.c().getString(R.string.preference_key_open_virus_cloud_scan);

    public VirusCloudScanModel(String str, Integer num) {
        super(str, num);
        setTrackStr("virus_cloudscan");
    }

    public String getDesc() {
        return null;
    }

    public int getIndex() {
        return 31;
    }

    public String getSummary() {
        return getContext().getString(R.string.summary_virus_cloudscan);
    }

    public String getTitle() {
        return getContext().getString(R.string.title_virus_cloudscan);
    }

    public void optimize(Context context) {
        b.b(this.cloudScanKey, true);
        setSafe(AbsModel.State.SAFE);
        runOnUiThread(new i(this, context));
    }

    public void scan() {
        boolean z = false;
        if (!Build.IS_INTERNATIONAL_BUILD && !b.a(this.cloudScanKey, false)) {
            z = true;
        }
        setSafe(z ? AbsModel.State.DANGER : AbsModel.State.SAFE);
    }
}
