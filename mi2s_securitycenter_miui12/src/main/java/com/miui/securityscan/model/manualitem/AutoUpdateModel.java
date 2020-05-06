package com.miui.securityscan.model.manualitem;

import android.content.Context;
import android.provider.Settings;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;

public class AutoUpdateModel extends AbsModel {
    private static final String SETTINGS_AUTO_UPDATE = "auto_update";

    public AutoUpdateModel(String str, Integer num) {
        super(str, num);
        setTrackStr("smart_update");
    }

    public String getDesc() {
        return null;
    }

    public int getIndex() {
        return 39;
    }

    public String getSummary() {
        return getContext().getString(R.string.summary_auto_update);
    }

    public String getTitle() {
        return getContext().getString(R.string.title_auto_update);
    }

    public void optimize(Context context) {
        Settings.Secure.putInt(getContext().getContentResolver(), SETTINGS_AUTO_UPDATE, 1);
        setSafe(AbsModel.State.SAFE);
        runOnUiThread(new a(this));
    }

    public void scan() {
        try {
            setSafe(Settings.Secure.getInt(getContext().getContentResolver(), SETTINGS_AUTO_UPDATE) == 1 ? AbsModel.State.SAFE : AbsModel.State.DANGER);
        } catch (Settings.SettingNotFoundException unused) {
        }
    }
}
