package com.miui.securityscan.model.manualitem.defaultapp;

import android.content.IntentFilter;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.manualitem.DefaultAppModel;
import com.xiaomi.stat.MiStat;
import miui.security.SecurityManager;

public class DefaultGalleryModel extends DefaultAppModel {
    public DefaultGalleryModel(String str, Integer num) {
        super(str, num);
        setTrackStr("default_gallery");
    }

    public int getIndex() {
        return 18;
    }

    /* access modifiers changed from: protected */
    public void initModel() {
        setDefaultPkgName(SecurityManager.SKIP_INTERCEPT_PACKAGE);
        setTypeName(getContext().getString(R.string.preferred_app_entries_gallery));
        try {
            IntentFilter intentFilter = new IntentFilter("android.intent.action.VIEW");
            intentFilter.addDataScheme(MiStat.Param.CONTENT);
            intentFilter.addDataType("image/*");
            setIntentFilter(intentFilter);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
    }
}
