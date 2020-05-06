package com.miui.firstaidkit.model.performance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import b.b.c.j.A;
import b.b.c.j.e;
import b.b.c.j.x;
import com.miui.cleanmaster.f;
import com.miui.cleanmaster.g;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;

public class InsufficientMemoryModel extends AbsModel {
    private static final String TAG = "InsufficientMemoryModel";

    public InsufficientMemoryModel(String str, Integer num) {
        super(str, num);
        setDelayOptimized(true);
        setTrackStr("insufficient_memory");
        setTrackIgnoreStr(getTrackStr() + "_ignore");
    }

    public String getButtonTitle() {
        return getContext().getString(R.string.first_aid_card_kadun_button1);
    }

    public String getDesc() {
        return "";
    }

    public int getIndex() {
        return 57;
    }

    public String getSummary() {
        return getContext().getString(R.string.first_aid_card_kadun_summary1);
    }

    public String getTitle() {
        return getContext().getString(R.string.first_aid_card_kadun_title);
    }

    public void optimize(Context context) {
        Intent intent = new Intent("miui.intent.action.GARBAGE_CLEANUP");
        intent.putExtra("enter_homepage_way", "security_firstaid_scanresult");
        if (f.a(context.getApplicationContext())) {
            if (!x.a(context, intent, 100)) {
                A.a(context, (int) R.string.app_not_installed_toast);
            }
        } else if (context instanceof Activity) {
            g.b((Activity) context, intent, 100, (Bundle) null);
        }
    }

    public void scan() {
        try {
            long c2 = e.c();
            Long valueOf = Long.valueOf(e.a());
            Log.d(TAG, "oriTotalMemory = " + c2 + "  oriFreeMemory = " + valueOf);
            setSafe((((((float) (valueOf.longValue() / 1024)) * 1.0f) / (((float) (c2 / 1024)) * 1.0f)) > 0.15f ? 1 : (((((float) (valueOf.longValue() / 1024)) * 1.0f) / (((float) (c2 / 1024)) * 1.0f)) == 0.15f ? 0 : -1)) < 0 ? AbsModel.State.DANGER : AbsModel.State.SAFE);
        } catch (Exception e) {
            Log.e(TAG, "scan error ", e);
        }
    }
}
