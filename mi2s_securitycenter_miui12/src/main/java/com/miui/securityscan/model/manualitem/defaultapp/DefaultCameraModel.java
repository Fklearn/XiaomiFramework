package com.miui.securityscan.model.manualitem.defaultapp;

import android.content.IntentFilter;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.manualitem.DefaultAppModel;

public class DefaultCameraModel extends DefaultAppModel {
    public DefaultCameraModel(String str, Integer num) {
        super(str, num);
        setTrackStr("default_camera");
    }

    public String getDesc() {
        return null;
    }

    public int getIndex() {
        return 17;
    }

    /* access modifiers changed from: protected */
    public void initModel() {
        setDefaultPkgName("com.android.camera");
        setTypeName(getContext().getString(R.string.preferred_app_entries_camera));
        setIntentFilter(new IntentFilter("android.media.action.IMAGE_CAPTURE"));
    }
}
