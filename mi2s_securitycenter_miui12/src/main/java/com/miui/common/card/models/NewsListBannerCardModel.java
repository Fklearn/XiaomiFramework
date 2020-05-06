package com.miui.common.card.models;

import com.miui.securitycenter.R;
import org.json.JSONObject;

public class NewsListBannerCardModel extends NewsCardModel {
    public NewsListBannerCardModel(JSONObject jSONObject, int i) {
        super(R.layout.card_layout_news_template_7, jSONObject, i);
    }

    public boolean validate() {
        return true;
    }
}
