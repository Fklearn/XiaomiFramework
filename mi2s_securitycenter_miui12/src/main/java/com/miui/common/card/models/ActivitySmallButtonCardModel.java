package com.miui.common.card.models;

import com.miui.securitycenter.R;
import org.json.JSONObject;

public class ActivitySmallButtonCardModel extends ActivityCardModel {
    public ActivitySmallButtonCardModel(JSONObject jSONObject, int i) {
        super(R.layout.card_layout_activity_template_6, jSONObject, i);
    }

    public boolean validate() {
        return true;
    }
}
