package com.miui.common.card.models;

import android.view.View;
import b.b.c.j.r;
import com.miui.common.card.BaseViewHolder;
import com.miui.common.card.models.FunctionCardModel;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;

public class FuncTopBannerNewCardModel extends FunctionCardModel {
    public FuncTopBannerNewCardModel() {
        this((AbsModel) null);
    }

    public FuncTopBannerNewCardModel(AbsModel absModel) {
        super(R.layout.card_layout_top_banner_new, absModel);
    }

    public BaseViewHolder createViewHolder(View view) {
        FunctionCardModel.FunctionViewHolder functionViewHolder = new FunctionCardModel.FunctionViewHolder(view);
        functionViewHolder.setIconDisplayOption(r.g);
        functionViewHolder.setImgDisplayOption(r.f1760d);
        return functionViewHolder;
    }
}
