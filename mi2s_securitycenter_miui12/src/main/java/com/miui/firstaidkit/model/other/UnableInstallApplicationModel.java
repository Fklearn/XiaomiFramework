package com.miui.firstaidkit.model.other;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import b.b.c.j.A;
import b.b.c.j.x;
import com.miui.cleanmaster.f;
import com.miui.securitycenter.R;
import com.miui.securityscan.i.g;
import com.miui.securityscan.i.q;
import com.miui.securityscan.model.AbsModel;

public class UnableInstallApplicationModel extends AbsModel {
    private long freeStorage;

    public UnableInstallApplicationModel(String str, Integer num) {
        super(str, num);
        setDelayOptimized(true);
        setTrackStr("unable_install_app");
        setTrackIgnoreStr(getTrackStr() + "_ignore");
    }

    public String getButtonTitle() {
        return getContext().getString(R.string.first_aid_card_unable_install_button);
    }

    public String getDesc() {
        return "";
    }

    public int getIndex() {
        return 54;
    }

    public String getSummary() {
        return getContext().getString(R.string.first_aid_card_unable_install_summary, new Object[]{g.a(this.freeStorage)});
    }

    public String getTitle() {
        return getContext().getString(R.string.first_aid_card_unable_install_title);
    }

    public void optimize(Context context) {
        Intent intent = new Intent("miui.intent.action.GARBAGE_CLEANUP");
        intent.putExtra("enter_homepage_way", "security_firstaid_scanresult");
        if (f.a(context.getApplicationContext())) {
            if (!x.a(context, intent, 100)) {
                A.a(context, (int) R.string.app_not_installed_toast);
            }
        } else if (context instanceof Activity) {
            com.miui.cleanmaster.g.b((Activity) context, intent, 100, (Bundle) null);
        }
    }

    public void scan() {
        q.a a2 = q.a(getContext());
        long j = a2.f7741b;
        float f = (float) a2.f7740a;
        float f2 = (((float) j) * 1.0f) / f;
        this.freeStorage = j;
        boolean z = true;
        if (f * 0.05f >= 3.0E9f ? j >= 3000000000L : f2 >= 0.05f) {
            z = false;
        }
        setSafe(z ? AbsModel.State.DANGER : AbsModel.State.SAFE);
    }
}
