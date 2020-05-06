package com.miui.securityscan.model.manualitem;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import miui.cloud.external.CloudSysHelper;

public class MiAccountModel extends AbsModel {
    private static final String ACCOUNT_TYPE_XIAOMI = "com.xiaomi";

    public MiAccountModel(String str, Integer num) {
        super(str, num);
        setDelayOptimized(true);
        setTrackStr("cloud_account");
    }

    public String getDesc() {
        return null;
    }

    public int getIndex() {
        return 29;
    }

    public String getSummary() {
        return getContext().getString(R.string.summary_cloud_account);
    }

    public String getTitle() {
        return getContext().getString(R.string.title_cloud_account);
    }

    public void optimize(Context context) {
        if (context instanceof Activity) {
            AccountManager.get(getContext()).addAccount("com.xiaomi", (String) null, (String[]) null, (Bundle) null, (Activity) context, (AccountManagerCallback) null, new Handler());
        }
    }

    public void scan() {
        setSafe(CloudSysHelper.isXiaomiAccountPresent(getContext()) ? AbsModel.State.SAFE : AbsModel.State.DANGER);
    }
}
