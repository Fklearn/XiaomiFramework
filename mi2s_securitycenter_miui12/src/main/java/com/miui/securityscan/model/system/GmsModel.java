package com.miui.securityscan.model.system;

import android.content.Context;
import com.miui.securitycenter.utils.c;
import com.miui.securityscan.model.AbsModel;

public class GmsModel extends AbsModel {
    public GmsModel(String str, Integer num) {
        super(str, num);
        setScanHide(true);
    }

    public String getDesc() {
        return "";
    }

    public int getIndex() {
        return 0;
    }

    public String getSummary() {
        return "";
    }

    public String getTitle() {
        return "";
    }

    public void optimize(Context context) {
        c.c(getContext());
    }

    public void scan() {
        setSafe(c.b(getContext()) ? AbsModel.State.SAFE : AbsModel.State.DANGER);
    }
}
