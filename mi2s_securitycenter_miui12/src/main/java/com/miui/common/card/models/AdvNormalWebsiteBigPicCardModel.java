package com.miui.common.card.models;

import android.view.View;
import b.b.c.j.r;
import com.miui.common.card.BaseViewHolder;
import com.miui.common.card.models.AdvCardModel;
import com.miui.securitycenter.R;
import org.json.JSONObject;

public class AdvNormalWebsiteBigPicCardModel extends AdvCardModel {
    public AdvNormalWebsiteBigPicCardModel(JSONObject jSONObject, int i) {
        super(R.layout.result_ad_template_3, jSONObject, i);
    }

    public BaseViewHolder createViewHolder(View view) {
        AdvCardModel.AdvViewHolder advViewHolder = new AdvCardModel.AdvViewHolder(view);
        advViewHolder.setIconDisplayOption(r.f1760d);
        return advViewHolder;
    }

    public boolean validate() {
        return true;
    }
}
