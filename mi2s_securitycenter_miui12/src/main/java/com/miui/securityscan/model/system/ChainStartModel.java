package com.miui.securityscan.model.system;

import android.content.Context;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.scanner.C0569p;
import com.miui.securityscan.scanner.C0570q;

public class ChainStartModel extends AbsModel {
    public ChainStartModel(String str, Integer num) {
        super(str, num);
    }

    public String getDesc() {
        return getContext().getString(R.string.item_mms);
    }

    public int getIndex() {
        return 0;
    }

    public String getSummary() {
        return "";
    }

    public String getTitle() {
        return getContext().getString(R.string.title_chain_start);
    }

    public void optimize(Context context) {
    }

    public void scan() {
        if (isSafe() == AbsModel.State.SAFE) {
            C0570q.b().a(C0570q.a.CLEANUP, getItemKey(), new C0569p(getTitle(), false));
        }
    }
}
