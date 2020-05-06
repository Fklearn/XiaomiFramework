package com.miui.securityscan.model.system;

import android.content.Context;
import b.b.b.b;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.scanner.C0569p;
import com.miui.securityscan.scanner.C0570q;

public class VirusDbModel extends AbsModel {
    private b antiVirusManager = b.a(getContext());

    public VirusDbModel(String str, Integer num) {
        super(str, num);
    }

    public String getDesc() {
        return getContext().getString(R.string.item_virus_lib);
    }

    public int getIndex() {
        return 0;
    }

    public String getSummary() {
        if (isSafe() == AbsModel.State.SAFE) {
            return null;
        }
        return getContext().getString(R.string.summary_virus_update);
    }

    public String getTitle() {
        return getContext().getString(isSafe() == AbsModel.State.SAFE ? R.string.title_virus_update_yes : R.string.title_virus_update_no);
    }

    public void optimize(Context context) {
        this.antiVirusManager.f();
        setSafe(AbsModel.State.SAFE);
        C0570q.b().a(C0570q.a.SYSTEM, getItemKey(), new C0569p(getTitle(), true));
    }

    public void scan() {
        setSafe(this.antiVirusManager.e() ? AbsModel.State.SAFE : AbsModel.State.DANGER);
        if (isSafe() == AbsModel.State.SAFE) {
            C0570q.b().a(C0570q.a.SYSTEM, getItemKey(), new C0569p(getTitle(), false));
        }
    }
}
