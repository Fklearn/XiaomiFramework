package com.miui.securityscan.model.system;

import android.content.Context;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.scanner.C0569p;
import com.miui.securityscan.scanner.C0570q;
import com.miui.support.provider.a;

public class MmsModel extends AbsModel {
    public MmsModel(String str, Integer num) {
        super(str, num);
    }

    public String getDesc() {
        return getContext().getString(R.string.item_mms);
    }

    public int getIndex() {
        return 0;
    }

    public String getSummary() {
        if (isSafe() == AbsModel.State.SAFE) {
            return null;
        }
        return getContext().getString(R.string.summary_mms);
    }

    public String getTitle() {
        return getContext().getString(isSafe() == AbsModel.State.SAFE ? R.string.title_mms_yes : R.string.title_mms_no);
    }

    public void optimize(Context context) {
        a.a(getContext(), true);
        setSafe(AbsModel.State.SAFE);
        C0570q.b().a(C0570q.a.SECURITY, getItemKey(), new C0569p(getTitle(), true));
    }

    public void scan() {
        setSafe(AbsModel.State.SAFE);
    }
}
