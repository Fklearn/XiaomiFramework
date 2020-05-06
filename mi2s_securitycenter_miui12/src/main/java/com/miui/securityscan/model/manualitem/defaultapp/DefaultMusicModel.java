package com.miui.securityscan.model.manualitem.defaultapp;

import android.content.IntentFilter;
import com.miui.networkassistant.config.Constants;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.manualitem.DefaultAppModel;
import com.xiaomi.stat.MiStat;

public class DefaultMusicModel extends DefaultAppModel {
    public DefaultMusicModel(String str, Integer num) {
        super(str, num);
        setTrackStr("default_player");
    }

    public int getIndex() {
        return 19;
    }

    /* access modifiers changed from: protected */
    public void initModel() {
        setDefaultPkgName("com.miui.player");
        setTypeName(getContext().getString(R.string.preferred_app_entries_music));
        try {
            IntentFilter intentFilter = new IntentFilter("android.intent.action.VIEW");
            intentFilter.addCategory(Constants.System.CATEGORY_DEFALUT);
            intentFilter.addDataScheme(MiStat.Param.CONTENT);
            intentFilter.addDataType("audio/*");
            setIntentFilter(intentFilter);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
    }
}
