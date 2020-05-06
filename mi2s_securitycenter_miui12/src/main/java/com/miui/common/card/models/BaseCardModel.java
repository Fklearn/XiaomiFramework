package com.miui.common.card.models;

import android.util.SparseIntArray;
import android.view.View;
import com.miui.common.card.BaseViewHolder;
import com.miui.securitycenter.R;

public abstract class BaseCardModel implements View.OnClickListener {
    private static final SparseIntArray TEMPLATE_TYPE = new SparseIntArray();
    public static final String TYPE_ACTIVITY = "003";
    public static final String TYPE_ADVERTISEMENT = "001";
    public static final String TYPE_ADVERTISEMENT_TEST = "0010";
    public static final String TYPE_CRAD = "006";
    public static final String TYPE_FUNCTION = "002";
    public static final String TYPE_LINE = "005";
    public static final String TYPE_NEWS = "004";
    public String button;
    private boolean canAutoScroll;
    public boolean canRrfreshFunctStatus;
    private int currentIndex = -1;
    public String dataId;
    private boolean defaultStatShow = true;
    public String icon;
    private boolean isOverseaChannel;
    private String language = "";
    protected transient int layoutId;
    public String negativeButtonText;
    public boolean noConvertView;
    public String positiveButtonText;
    public boolean subVisible;
    public String summary;
    public String title;
    private String type;

    static {
        TEMPLATE_TYPE.put(R.layout.card_layout_top, 0);
        TEMPLATE_TYPE.put(R.layout.card_layout_line, 1);
        TEMPLATE_TYPE.put(R.layout.card_layout_left_banner, 2);
        TEMPLATE_TYPE.put(R.layout.card_layout_list_banner, 3);
        TEMPLATE_TYPE.put(R.layout.card_layout_list_title, 4);
        TEMPLATE_TYPE.put(R.layout.card_layout_button_bottom_banner, 5);
        TEMPLATE_TYPE.put(R.layout.card_layout_top_banner, 6);
        TEMPLATE_TYPE.put(R.layout.card_layout_right_checkbox, 7);
        TEMPLATE_TYPE.put(R.layout.card_layout_bottom_button, 8);
        TEMPLATE_TYPE.put(R.layout.card_layout_list_title_checkbox, 9);
        TEMPLATE_TYPE.put(R.layout.card_layout_adv_list_title, 10);
        TEMPLATE_TYPE.put(R.layout.card_layout_consume_power_rank, 11);
        TEMPLATE_TYPE.put(R.layout.card_layout_flow_rank, 12);
        TEMPLATE_TYPE.put(R.layout.card_layout_placeholder, 13);
        TEMPLATE_TYPE.put(R.layout.scanresult_card_layout_top, 14);
        TEMPLATE_TYPE.put(R.layout.card_layout_cloud_space, 15);
        TEMPLATE_TYPE.put(R.layout.result_ad_template_3, 16);
        TEMPLATE_TYPE.put(R.layout.result_ad_template_40, 17);
        TEMPLATE_TYPE.put(R.layout.result_ad_template_4, 18);
        TEMPLATE_TYPE.put(R.layout.result_template_ad_fb, 19);
        TEMPLATE_TYPE.put(R.layout.card_layout_news_template_7, 20);
        TEMPLATE_TYPE.put(R.layout.card_layout_activity_template_6, 21);
        TEMPLATE_TYPE.put(R.layout.card_layout_activity_template_7, 22);
        TEMPLATE_TYPE.put(R.layout.card_layout_top_banner_new, 23);
        TEMPLATE_TYPE.put(R.layout.card_layout_scan_result_bottom_new, 24);
        TEMPLATE_TYPE.put(R.layout.card_layout_grid_six_parent, 25);
        TEMPLATE_TYPE.put(R.layout.card_layout_grid_nine_parent_colorful, 26);
        TEMPLATE_TYPE.put(R.layout.card_layout_top_banner_new_2, 27);
        TEMPLATE_TYPE.put(R.layout.result_template_ad_admob_context, 28);
        TEMPLATE_TYPE.put(R.layout.result_template_ad_admob_install, 29);
        TEMPLATE_TYPE.put(R.layout.result_template_ad_global_empty, 30);
        TEMPLATE_TYPE.put(R.layout.result_template_ad_columbus, 31);
        TEMPLATE_TYPE.put(R.layout.firstaidkit_scanresult_card_layout_top, 32);
        TEMPLATE_TYPE.put(R.layout.firstaidkit_scanresult_card_layout_bottom, 33);
        TEMPLATE_TYPE.put(R.layout.firstaidkit_scanresult_card_layout_feedback, 34);
        TEMPLATE_TYPE.put(R.layout.result_ad_template_5, 35);
        TEMPLATE_TYPE.put(R.layout.result_ad_template_25, 36);
        TEMPLATE_TYPE.put(R.layout.result_ad_template_31, 37);
        TEMPLATE_TYPE.put(R.layout.card_layout_top_banner_scroll, 38);
        TEMPLATE_TYPE.put(R.layout.phone_manage_list_item_card, 39);
        TEMPLATE_TYPE.put(R.layout.phone_manage_recommend_item_card, 40);
        TEMPLATE_TYPE.put(R.layout.phone_manager_card_layout_list_title, 41);
        TEMPLATE_TYPE.put(R.layout.phone_manage_card_banner_layout, 42);
        TEMPLATE_TYPE.put(R.layout.phone_manager_recommend_layout_title, 43);
        TEMPLATE_TYPE.put(R.layout.securityscan_bottom_place_card_layout, 44);
        TEMPLATE_TYPE.put(R.layout.scanresult_card_layout_normal_new, 45);
    }

    public BaseCardModel(int i) {
        this.layoutId = i;
    }

    public static int getLayoutType(int i) {
        return TEMPLATE_TYPE.get(i);
    }

    public static int getLayoutTypeCount() {
        return TEMPLATE_TYPE.size();
    }

    public BaseViewHolder createViewHolder(View view) {
        return new BaseViewHolder(view);
    }

    public String getButton() {
        return this.button;
    }

    public int getCurrentIndex() {
        return this.currentIndex;
    }

    public String getDataId() {
        return this.dataId;
    }

    public String getIcon() {
        return this.icon;
    }

    public String getLanguage() {
        return this.language;
    }

    public int getLayoutId() {
        return this.layoutId;
    }

    public int getLayoutIdType() {
        return TEMPLATE_TYPE.get(this.layoutId);
    }

    public String getNegativeButtonText() {
        return this.negativeButtonText;
    }

    public String getPositiveButtonText() {
        return this.positiveButtonText;
    }

    public String getSummary() {
        return this.summary;
    }

    public String getTitle() {
        return this.title;
    }

    public boolean isCanAutoScroll() {
        return this.canAutoScroll;
    }

    public boolean isDefaultStatShow() {
        return this.defaultStatShow;
    }

    public boolean isOverseaChannel() {
        return this.isOverseaChannel;
    }

    public void onClick(View view) {
    }

    public void setButton(String str) {
        this.button = str;
    }

    public void setCanAutoScroll(boolean z) {
        this.canAutoScroll = z;
    }

    public void setCurrentIndex(int i) {
        this.currentIndex = i;
    }

    public void setDataId(String str) {
        this.dataId = str;
    }

    public void setDefaultStatShow(boolean z) {
        this.defaultStatShow = z;
    }

    public void setIcon(String str) {
        this.icon = str;
    }

    public void setLanguage(String str) {
        this.language = str;
    }

    public void setLayoutId(int i) {
        this.layoutId = i;
    }

    public void setNegativeButtonText(String str) {
        this.negativeButtonText = str;
    }

    public void setOverseaChannel(boolean z) {
        this.isOverseaChannel = z;
    }

    public void setPositiveButtonText(String str) {
        this.positiveButtonText = str;
    }

    public void setSubVisible(boolean z) {
        this.subVisible = z;
    }

    public void setSummary(String str) {
        this.summary = str;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public void startAutoScroll() {
    }

    public void stopAutoScroll() {
    }

    public abstract boolean validate();
}
