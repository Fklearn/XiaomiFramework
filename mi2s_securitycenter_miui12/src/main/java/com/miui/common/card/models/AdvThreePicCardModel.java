package com.miui.common.card.models;

import android.view.View;
import b.b.c.j.r;
import com.miui.common.card.BaseViewHolder;
import com.miui.common.card.models.AdvCardModel;
import org.json.JSONObject;

public class AdvThreePicCardModel extends AdvCardModel {
    public AdvThreePicCardModel(int i, JSONObject jSONObject, int i2) {
        super(i, jSONObject, i2);
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
