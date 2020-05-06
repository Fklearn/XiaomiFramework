package com.miui.common.card.models;

import com.miui.securitycenter.R;

public class LineCardModel extends BaseCardModel {
    public LineCardModel() {
        super(R.layout.card_layout_line);
    }

    public boolean validate() {
        return true;
    }
}
