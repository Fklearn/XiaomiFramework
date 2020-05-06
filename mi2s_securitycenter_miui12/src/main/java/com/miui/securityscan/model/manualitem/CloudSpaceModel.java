package com.miui.securityscan.model.manualitem;

import android.content.Context;
import android.util.Log;
import b.b.c.j.B;
import com.miui.securitycenter.R;
import com.miui.securityscan.M;
import com.miui.securityscan.model.AbsModel;
import miui.cloud.CloudSyncUtils;
import miui.cloud.util.SyncStatusHelper;
import miui.os.Build;

public class CloudSpaceModel extends AbsModel {
    private static final String TAG = "CloudSpaceModel";
    private int status = 0;

    public CloudSpaceModel(String str, Integer num) {
        super(str, num);
        setDelayOptimized(true);
    }

    public String getDesc() {
        return null;
    }

    public int getIndex() {
        return 3;
    }

    public String getSummary() {
        int i;
        Context context;
        if (this.status == 1) {
            context = getContext();
            i = R.string.summary_cloud_space_almost_full;
        } else {
            context = getContext();
            i = R.string.summary_cloud_space_full;
        }
        return context.getString(i);
    }

    public String getTitle() {
        int i;
        Context context;
        if (this.status == 1) {
            context = getContext();
            i = R.string.title_cloud_space_almost_full;
        } else {
            context = getContext();
            i = R.string.title_cloud_space_full;
        }
        return context.getString(i);
    }

    public void optimize(Context context) {
        M.b(true);
        try {
            CloudSyncUtils.startMiCloudMemberActivity(context, getContext().getPackageName());
        } catch (Exception e) {
            Log.e(TAG, "optimize error: ", e);
        }
    }

    public void scan() {
        if (Build.IS_INTERNATIONAL_BUILD || B.g()) {
            setSafe(AbsModel.State.SAFE);
            return;
        }
        try {
            this.status = SyncStatusHelper.getSyncStatus(getContext());
            boolean z = false;
            boolean z2 = this.status == 1;
            if (this.status == 2) {
                z = true;
            }
            setSafe(z2 ? AbsModel.State.DANGER_MINOR : z ? AbsModel.State.DANGER : AbsModel.State.SAFE);
            if (isSafe() != AbsModel.State.SAFE) {
                setTrackStr(z2 ? "cloud_space_almost_full" : "cloud_space_full");
            }
        } catch (Exception e) {
            Log.e(TAG, "scan error: ", e);
        }
    }
}
