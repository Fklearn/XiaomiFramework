package com.miui.securityscan.model.manualitem;

import android.content.Context;
import android.content.Intent;
import com.miui.securitycenter.R;
import com.miui.securitycenter.h;
import com.miui.securitycenter.service.NotificationService;
import com.miui.securityscan.model.AbsModel;

public class SecurityNotificationModel extends AbsModel {
    public SecurityNotificationModel(String str, Integer num) {
        super(str, num);
        setTrackStr("show_notification");
    }

    public String getDesc() {
        return null;
    }

    public int getIndex() {
        return 30;
    }

    public String getSummary() {
        return getContext().getString(R.string.summary_show_notification);
    }

    public String getTitle() {
        return getContext().getString(R.string.title_show_notification);
    }

    public void optimize(Context context) {
        h.a(getContext().getContentResolver(), true);
        context.startService(new Intent(context, NotificationService.class).putExtra("notify", false));
        setSafe(AbsModel.State.SAFE);
        runOnUiThread(new g(this, context));
    }

    public void scan() {
        setSafe(!(h.a(getContext().getContentResolver()) ^ true) ? AbsModel.State.SAFE : AbsModel.State.DANGER);
    }
}
