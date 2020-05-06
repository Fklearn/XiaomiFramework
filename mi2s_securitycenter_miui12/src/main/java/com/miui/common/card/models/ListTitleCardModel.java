package com.miui.common.card.models;

import android.content.res.Resources;
import android.view.View;
import com.miui.common.card.BaseViewHolder;
import com.miui.securitycenter.R;

public class ListTitleCardModel extends TitleCardModel {

    class ListTitleViewHolder extends BaseViewHolder {
        public ListTitleViewHolder(View view) {
            super(view);
        }

        public void fillData(View view, BaseCardModel baseCardModel, int i) {
            int i2;
            int i3;
            super.fillData(view, baseCardModel, i);
            Resources resources = view.getResources();
            if (((ListTitleCardModel) baseCardModel).isHomePageFunc()) {
                view.setBackground(resources.getDrawable(R.drawable.hp_card_bg_no_shadow_top));
                i2 = view.getPaddingStart();
                i3 = R.dimen.hp_card_title_padding_top;
            } else {
                view.setBackground(resources.getDrawable(R.drawable.card_bg_no_shadow_top));
                i2 = view.getPaddingStart();
                i3 = R.dimen.result_card_list_padding_top;
            }
            view.setPaddingRelative(i2, resources.getDimensionPixelSize(i3), view.getPaddingEnd(), resources.getDimensionPixelSize(R.dimen.power_card_title_margin_bottom));
        }
    }

    public ListTitleCardModel() {
        super(R.layout.card_layout_list_title);
    }

    public BaseViewHolder createViewHolder(View view) {
        return new ListTitleViewHolder(view);
    }

    public boolean validate() {
        return true;
    }
}
