package com.miui.common.card.models;

import com.miui.securitycenter.R;

public class TopCardModel extends BaseCardModel {
    public TopCardModel() {
        super(R.layout.card_layout_top);
    }

    public boolean validate() {
        return true;
    }
}
