package com.miui.securityscan.model.manualitem;

import android.content.Context;
import com.miui.securitycenter.R;
import com.miui.securitycenter.h;
import com.miui.securityscan.model.AbsModel;

public class CleanerDbUpdateModel extends AbsModel {
    public CleanerDbUpdateModel(String str, Integer num) {
        super(str, num);
        setTrackStr("garbage_lib");
    }

    public String getDesc() {
        return null;
    }

    public int getIndex() {
        return 23;
    }

    public String getSummary() {
        return getContext().getString(R.string.summary_garbage_lib);
    }

    public String getTitle() {
        return getContext().getString(R.string.title_garbage_lib);
    }

    public void optimize(Context context) {
        h.b(getContext(), true);
        setSafe(AbsModel.State.SAFE);
        runOnUiThread(new c(this, context));
    }

    public void scan() {
        setSafe(h.f(getContext()) ? AbsModel.State.SAFE : AbsModel.State.DANGER);
    }
}
