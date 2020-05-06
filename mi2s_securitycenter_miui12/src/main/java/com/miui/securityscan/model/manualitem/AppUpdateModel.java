package com.miui.securityscan.model.manualitem;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import b.b.c.j.A;
import b.b.c.j.x;
import com.miui.securitycenter.R;
import com.miui.securityscan.M;
import com.miui.securityscan.model.AbsModel;
import miui.os.Build;

public class AppUpdateModel extends AbsModel {
    private int updateCount = 0;

    public AppUpdateModel(String str, Integer num) {
        super(str, num);
        setTrackStr("app_update");
        setDelayOptimized(true);
    }

    public String getDesc() {
        return null;
    }

    public int getIndex() {
        return 27;
    }

    public String getSummary() {
        Resources resources = getContext().getResources();
        int i = this.updateCount;
        return resources.getQuantityString(R.plurals.summary_app_update, i, new Object[]{Integer.valueOf(i)});
    }

    public String getTitle() {
        return getContext().getString(R.string.title_app_update);
    }

    public void optimize(Context context) {
        Intent intent = new Intent("com.xiaomi.market.UPDATE_APP_LIST");
        intent.putExtra("back", true);
        if (!x.a(context, intent, 100)) {
            A.a(context, (int) R.string.app_not_installed_toast);
        }
    }

    public void scan() {
        this.updateCount = M.d();
        setSafe(!(!Build.IS_INTERNATIONAL_BUILD && this.updateCount > 0) ? AbsModel.State.SAFE : AbsModel.State.DANGER);
    }
}
