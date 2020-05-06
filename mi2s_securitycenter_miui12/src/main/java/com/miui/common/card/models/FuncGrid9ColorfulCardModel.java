package com.miui.common.card.models;

import android.view.View;
import b.c.a.b.d;
import com.miui.common.card.BaseViewHolder;
import com.miui.common.card.models.FuncGridBaseCardModel;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;

public class FuncGrid9ColorfulCardModel extends FuncGridBaseCardModel {
    public FuncGrid9ColorfulCardModel() {
        this((AbsModel) null);
    }

    public FuncGrid9ColorfulCardModel(AbsModel absModel) {
        super(R.layout.card_layout_grid_nine_parent_colorful, absModel);
    }

    public BaseViewHolder createViewHolder(View view) {
        FuncGridBaseCardModel.FuncGridBaseViewHolder funcGridBaseViewHolder = new FuncGridBaseCardModel.FuncGridBaseViewHolder(view);
        d.a aVar = new d.a();
        aVar.c((int) R.drawable.phone_manage_default_selector);
        aVar.b((int) R.drawable.phone_manage_default_selector);
        aVar.a((int) R.drawable.phone_manage_default_selector);
        aVar.a(true);
        aVar.b(true);
        aVar.c(true);
        funcGridBaseViewHolder.options = aVar.a();
        return funcGridBaseViewHolder;
    }
}
