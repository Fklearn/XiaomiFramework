package com.miui.common.card.models;

import com.miui.securitycenter.R;

public class PlaceHolderCardModel extends BaseCardModel {
    public PlaceHolderCardModel() {
        super(R.layout.card_layout_placeholder);
    }

    public boolean validate() {
        return true;
    }
}
