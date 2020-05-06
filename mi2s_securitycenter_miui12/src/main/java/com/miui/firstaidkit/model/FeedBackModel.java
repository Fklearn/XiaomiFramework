package com.miui.firstaidkit.model;

import android.content.Context;
import android.content.Intent;
import b.b.c.j.A;
import b.b.c.j.x;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;

public class FeedBackModel extends AbsModel {
    public FeedBackModel(String str, Integer num) {
        super(str, num);
        setDelayOptimized(true);
        setTrackStr("first_aid_feed_back");
    }

    public String getButtonTitle() {
        return getContext().getString(R.string.first_aid_result_feedback_button);
    }

    public String getDesc() {
        return null;
    }

    public int getIndex() {
        return 0;
    }

    public String getSummary() {
        return "";
    }

    public String getTitle() {
        return getContext().getString(R.string.first_aid_result_feedback_summary);
    }

    public void optimize(Context context) {
        Intent intent = new Intent("miui.intent.action.BUGREPORT");
        intent.putExtra("appTitle", getContext().getString(R.string.first_aid_activity_title));
        if (!x.a(context, intent, 100)) {
            A.a(context, (int) R.string.app_not_installed_toast);
        }
    }

    public void scan() {
    }
}
