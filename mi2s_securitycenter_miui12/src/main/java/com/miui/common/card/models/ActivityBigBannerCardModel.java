package com.miui.common.card.models;

import android.view.View;
import b.b.c.j.r;
import com.miui.common.card.BaseViewHolder;
import com.miui.common.card.models.ActivityCardModel;
import com.miui.securitycenter.R;
import org.json.JSONObject;

public class ActivityBigBannerCardModel extends ActivityCardModel {
    public ActivityBigBannerCardModel(JSONObject jSONObject, int i) {
        super(R.layout.card_layout_activity_template_7, jSONObject, i);
    }

    public BaseViewHolder createViewHolder(View view) {
        ActivityCardModel.ActivityViewHolder activityViewHolder = new ActivityCardModel.ActivityViewHolder(view);
        activityViewHolder.setIconDisplayOption(r.f1760d);
        return activityViewHolder;
    }

    public boolean validate() {
        return true;
    }
}
