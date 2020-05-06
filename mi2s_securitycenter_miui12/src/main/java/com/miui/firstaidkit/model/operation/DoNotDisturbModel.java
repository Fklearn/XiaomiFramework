package com.miui.firstaidkit.model.operation;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.MiuiSettings;
import android.util.Log;
import b.b.c.j.A;
import b.b.c.j.x;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;

public class DoNotDisturbModel extends AbsModel {
    public static final int MIUI_IMPORTANT_INTERRUPTIONS = 1;
    private static final String TAG = "DoNotDisturbModel";

    public DoNotDisturbModel(String str, Integer num) {
        super(str, num);
        setDelayOptimized(true);
        setTrackStr("do_not_disturb");
        setTrackIgnoreStr(getTrackStr() + "_ignore");
    }

    public String getButtonTitle() {
        return getContext().getString(R.string.button_do_not_disturb);
    }

    public String getDesc() {
        return "";
    }

    public int getIndex() {
        return 45;
    }

    public String getSummary() {
        return getContext().getString(R.string.summary_do_not_disturb);
    }

    public String getTitle() {
        return getContext().getString(R.string.title_do_not_disturb);
    }

    public void ignore() {
    }

    public void optimize(Context context) {
        String str;
        if (MiuiSettings.SilenceMode.isSupported) {
            Log.d(TAG, "optimize isSupported: true");
            str = "com.android.settings.Settings$MiuiSilentModeAcivity";
        } else {
            Log.d(TAG, "optimize isSupported: false");
            str = "com.android.settings.dndmode.DoNotDisturbModeActivity";
        }
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.android.settings", str));
        if (!x.a(context, intent, 100)) {
            A.a(context, (int) R.string.app_not_installed_toast);
        }
    }

    public void scan() {
        boolean z = true;
        if (MiuiSettings.SilenceMode.getZenMode(getContext()) != 1) {
            z = false;
        }
        setSafe(z ? AbsModel.State.DANGER : AbsModel.State.SAFE);
    }
}
