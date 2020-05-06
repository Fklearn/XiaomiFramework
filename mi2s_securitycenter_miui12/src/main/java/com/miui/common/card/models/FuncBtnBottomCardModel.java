package com.miui.common.card.models;

import android.view.View;
import b.b.c.j.r;
import com.miui.common.card.BaseViewHolder;
import com.miui.common.card.models.FunctionCardModel;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;

public class FuncBtnBottomCardModel extends FunctionCardModel {
    public FuncBtnBottomCardModel() {
        this((AbsModel) null);
    }

    public FuncBtnBottomCardModel(AbsModel absModel) {
        super(R.layout.card_layout_button_bottom_banner, absModel);
    }

    public BaseViewHolder createViewHolder(View view) {
        FunctionCardModel.FunctionViewHolder functionViewHolder = new FunctionCardModel.FunctionViewHolder(view);
        functionViewHolder.setIconDisplayOption(r.h);
        return functionViewHolder;
    }
}
