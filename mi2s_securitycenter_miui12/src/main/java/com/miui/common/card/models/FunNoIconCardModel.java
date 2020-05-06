package com.miui.common.card.models;

import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;

public class FunNoIconCardModel extends FunctionCardModel {
    public FunNoIconCardModel() {
        this((AbsModel) null);
    }

    public FunNoIconCardModel(AbsModel absModel) {
        super(R.layout.scanresult_card_layout_normal_new, absModel);
    }
}
