package com.miui.securityscan.model.manualitem.defaultapp;

import android.content.IntentFilter;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.model.manualitem.DefaultAppModel;
import miui.os.Build;

public class DefaultEmailModel extends DefaultAppModel {
    public DefaultEmailModel(String str, Integer num) {
        super(str, num);
        setTrackStr("default_email");
    }

    public int getIndex() {
        return 20;
    }

    /* access modifiers changed from: protected */
    public void initModel() {
        setDefaultPkgName("com.android.email");
        setTypeName(getContext().getString(R.string.preferred_app_entries_email));
        IntentFilter intentFilter = new IntentFilter("android.intent.action.SENDTO");
        intentFilter.addDataScheme("mailto");
        setIntentFilter(intentFilter);
    }

    public void scan() {
        if (Build.IS_INTERNATIONAL_BUILD) {
            setSafe(AbsModel.State.SAFE);
        } else {
            super.scan();
        }
    }
}
