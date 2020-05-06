package com.miui.securityscan.model.system;

import android.content.Context;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.scanner.C0569p;
import com.miui.securityscan.scanner.C0570q;
import com.miui.support.provider.b;

public class InstallMonitorModel extends AbsModel {
    public InstallMonitorModel(String str, Integer num) {
        super(str, num);
    }

    public String getDesc() {
        return getContext().getString(R.string.item_install_monitor);
    }

    public int getIndex() {
        return 0;
    }

    public String getSummary() {
        if (isSafe() == AbsModel.State.SAFE) {
            return null;
        }
        return getContext().getString(R.string.summary_install_virus);
    }

    public String getTitle() {
        return getContext().getString(isSafe() == AbsModel.State.SAFE ? R.string.title_install_virus_yes : R.string.title_install_virus_no);
    }

    public void optimize(Context context) {
        b.a(getContext(), true);
        setSafe(AbsModel.State.SAFE);
        C0570q.b().a(C0570q.a.SYSTEM, getItemKey(), new C0569p(getTitle(), true));
    }

    public void scan() {
        setSafe(b.a(getContext()) ? AbsModel.State.SAFE : AbsModel.State.DANGER);
        if (isSafe() == AbsModel.State.SAFE) {
            C0570q.b().a(C0570q.a.SYSTEM, getItemKey(), new C0569p(getTitle(), false));
        }
    }
}
