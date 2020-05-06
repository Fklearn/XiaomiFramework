package com.miui.securityscan.model.manualitem;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import b.b.c.j.A;
import b.b.c.j.i;
import b.b.c.j.x;
import com.miui.securitycenter.R;
import com.miui.securityscan.M;
import com.miui.securityscan.i.m;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.scanner.C0569p;
import com.miui.securityscan.scanner.C0570q;
import java.util.Map;

public class MiuiUpdaterModel extends AbsModel {
    private static final String MIUI_UPDATE_KEY = "MIUI_UPDATE";
    private static final String UPDATER_MAIN_ACTIVITY = "com.android.updater.MainActivity";
    private static final String UPDATER_PKGNAME = "com.android.updater";
    private String newVersion = "";

    public MiuiUpdaterModel(String str, Integer num) {
        super(str, num);
        setDelayOptimized(true);
        setTrackStr("miui_update");
    }

    public String getDesc() {
        return null;
    }

    public int getIndex() {
        return 1;
    }

    public String getSummary() {
        return getContext().getString(R.string.summary_miui_update, new Object[]{this.newVersion});
    }

    public String getTitle() {
        return getContext().getString(R.string.title_miui_update);
    }

    public void optimize(Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(UPDATER_PKGNAME, UPDATER_MAIN_ACTIVITY));
        if (!x.a(context, intent, 100)) {
            A.a(context, (int) R.string.app_not_installed_toast);
        }
    }

    public void scan() {
        boolean j = i.j(getContext());
        this.newVersion = M.f();
        setSafe((!j || m.c(MIUI_UPDATE_KEY)) ? AbsModel.State.SAFE : AbsModel.State.DANGER);
        if (isSafe() == AbsModel.State.SAFE) {
            C0570q.b().a(C0570q.a.SECURITY, getItemKey(), new C0569p(getContext().getString(R.string.title_miui_update_latest), false));
            return;
        }
        Map<String, C0569p> a2 = C0570q.b().a(C0570q.a.SECURITY);
        if (a2.containsKey(getItemKey())) {
            a2.remove(getItemKey());
        }
    }
}
