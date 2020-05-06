package com.miui.securityscan.model.system;

import android.content.Context;
import android.provider.Settings;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.scanner.C0569p;
import com.miui.securityscan.scanner.C0570q;

public class AutoDownloadModel extends AbsModel {
    private static final String SETTINGS_AUTO_DOWNLOAD = "auto_download";
    private static final int SETTINGS_AUTO_DOWNLOAD_OFF = 0;
    private static final int SETTINGS_AUTO_DOWNLOAD_ON = 1;

    public AutoDownloadModel(String str, Integer num) {
        super(str, num);
    }

    public String getDesc() {
        return getContext().getString(R.string.item_system_auto_download);
    }

    public int getIndex() {
        return 0;
    }

    public String getSummary() {
        return "";
    }

    public String getTitle() {
        return getContext().getString(R.string.title_system_auto_download);
    }

    public void optimize(Context context) {
        Settings.Secure.putInt(getContext().getContentResolver(), SETTINGS_AUTO_DOWNLOAD, 1);
        setSafe(AbsModel.State.SAFE);
        C0570q.b().a(C0570q.a.SYSTEM, getItemKey(), new C0569p(getTitle(), true));
    }

    public void scan() {
        setSafe(Settings.Secure.getInt(getContext().getContentResolver(), SETTINGS_AUTO_DOWNLOAD, 0) == 1 ? AbsModel.State.SAFE : AbsModel.State.DANGER);
        if (isSafe() == AbsModel.State.SAFE) {
            C0570q.b().a(C0570q.a.SYSTEM, getItemKey(), new C0569p(getTitle(), false));
        }
    }
}
