package com.miui.networkassistant.netdiagnose.item;

import android.content.Context;
import com.miui.networkassistant.netdiagnose.AbstractNetworkDiagoneItem;

public class TitleCheck extends AbstractNetworkDiagoneItem {
    private String mTitle = "";

    public TitleCheck(Context context, String str) {
        super(context);
        this.mTitle = str;
    }

    public void check() {
    }

    public AbstractNetworkDiagoneItem.FixedResult fix() {
        return AbstractNetworkDiagoneItem.FixedResult.SUCCESS;
    }

    public String getFixingWaitProgressDlgMsg() {
        return "";
    }

    public String getItemName() {
        return this.mTitle;
    }

    public String getItemSolution() {
        return "";
    }

    public String getItemSummary() {
        return "";
    }
}
