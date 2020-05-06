package com.miui.securityscan.model.system;

import android.content.Context;
import android.provider.Settings;
import b.b.c.j.y;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;

public class UsbModel extends AbsModel {
    public UsbModel(String str, Integer num) {
        super(str, num);
    }

    public String getDesc() {
        return getContext().getString(R.string.item_usb_debug);
    }

    public int getIndex() {
        return 0;
    }

    public String getSummary() {
        if (isSafe() == AbsModel.State.SAFE) {
            return null;
        }
        return getContext().getString(R.string.summary_usb);
    }

    public String getTitle() {
        return getContext().getString(isSafe() == AbsModel.State.SAFE ? R.string.title_usb_yes : R.string.title_usb_no);
    }

    public void optimize(Context context) {
        Settings.Secure.putInt(getContext().getContentResolver(), "adb_enabled", 0);
        setSafe(AbsModel.State.SAFE);
    }

    public void scan() {
        AbsModel.State state;
        boolean z = false;
        if (y.a("ro.debuggable", 0) != 1) {
            if (Settings.Secure.getInt(getContext().getContentResolver(), "adb_enabled", 0) == 1) {
                z = true;
            }
            if (z) {
                state = AbsModel.State.DANGER;
                setSafe(state);
            }
        }
        state = AbsModel.State.SAFE;
        setSafe(state);
    }
}
