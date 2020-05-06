package com.miui.securityscan.model.manualitem;

import android.content.Context;
import com.miui.securitycenter.R;
import com.miui.securitycenter.h;
import com.miui.securityscan.model.AbsModel;
import miui.os.Build;

public class CleanerCloudScanModel extends AbsModel {
    public CleanerCloudScanModel(String str, Integer num) {
        super(str, num);
        setTrackStr("cleaner_cloudscan");
    }

    public String getDesc() {
        return null;
    }

    public int getIndex() {
        return 22;
    }

    public String getSummary() {
        return getContext().getString(R.string.summary_cleaner_cloudscan);
    }

    public String getTitle() {
        return getContext().getString(R.string.title_cleaner_cloudscan);
    }

    public void optimize(Context context) {
        h.e(getContext(), true);
        setSafe(AbsModel.State.SAFE);
        runOnUiThread(new b(this, context));
    }

    public void scan() {
        setSafe(!(!Build.IS_INTERNATIONAL_BUILD && !h.k(getContext())) ? AbsModel.State.SAFE : AbsModel.State.DANGER);
    }
}
