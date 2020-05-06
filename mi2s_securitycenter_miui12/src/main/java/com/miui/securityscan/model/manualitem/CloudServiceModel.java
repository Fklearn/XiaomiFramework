package com.miui.securityscan.model.manualitem;

import android.content.Context;
import android.util.Log;
import b.b.c.j.B;
import com.miui.securitycenter.R;
import com.miui.securityscan.M;
import com.miui.securityscan.model.AbsModel;
import miui.cloud.external.CloudSysHelper;

public class CloudServiceModel extends AbsModel {
    private static final String TAG = "CloudServiceModel";

    public CloudServiceModel(String str, Integer num) {
        super(str, num);
        setTrackStr("cloud_service");
        setDelayOptimized(true);
    }

    public String getDesc() {
        return null;
    }

    public int getIndex() {
        return 2;
    }

    public String getSummary() {
        return getContext().getString(R.string.summary_cloud_service);
    }

    public String getTitle() {
        return getContext().getString(R.string.title_cloud_service);
    }

    public void optimize(Context context) {
        M.b(true);
        try {
            CloudSysHelper.promptEnableAllMiCloudSync(context);
        } catch (Exception e) {
            Log.e(TAG, "optimize error: ", e);
        }
    }

    public void scan() {
        setSafe((!B.g() && CloudSysHelper.isMiCloudMainSyncItemsOff(getContext())) ? AbsModel.State.DANGER : AbsModel.State.SAFE);
    }
}
