package com.miui.common.card.models;

import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;
import b.b.c.j.r;
import com.miui.common.card.BaseViewHolder;
import com.miui.common.card.models.FunctionCardModel;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;

public class FuncTopBannerCardModel extends FunctionCardModel {

    class FuncTopBannerViewHolder extends FunctionCardModel.FunctionViewHolder {
        public FuncTopBannerViewHolder(View view) {
            super(view);
        }

        public void fillData(View view, BaseCardModel baseCardModel, int i) {
            int i2;
            TextView textView;
            super.fillData(view, baseCardModel, i);
            Resources resources = view.getResources();
            if (((FuncTopBannerCardModel) baseCardModel).isHomePageFunc()) {
                view.setBackground(resources.getDrawable(R.drawable.hp_card_bg_no_shadow_selector));
                view.setPaddingRelative(view.getPaddingStart(), resources.getDimensionPixelSize(R.dimen.hp_card_layout_line_height_half), view.getPaddingEnd(), resources.getDimensionPixelSize(R.dimen.hp_top_banner_card_padding_bottom));
                textView = this.summaryView;
                i2 = R.dimen.hp_card_summary_size;
            } else {
                view.setBackground(resources.getDrawable(R.drawable.card_bg_no_shadow_selector));
                view.setPaddingRelative(view.getPaddingStart(), resources.getDimensionPixelSize(R.dimen.card_layout_line_height_half), view.getPaddingEnd(), resources.getDimensionPixelSize(R.dimen.result_func_item_padding_bottom));
                textView = this.summaryView;
                i2 = R.dimen.card_summary_text_size;
            }
            textView.setTextSize(0, (float) resources.getDimensionPixelSize(i2));
        }
    }

    public FuncTopBannerCardModel() {
        this((AbsModel) null);
    }

    public FuncTopBannerCardModel(AbsModel absModel) {
        super(R.layout.card_layout_top_banner, absModel);
    }

    public BaseViewHolder createViewHolder(View view) {
        FuncTopBannerViewHolder funcTopBannerViewHolder = new FuncTopBannerViewHolder(view);
        funcTopBannerViewHolder.setIconDisplayOption(r.g);
        funcTopBannerViewHolder.setImgDisplayOption(r.f1760d);
        return funcTopBannerViewHolder;
    }
}
