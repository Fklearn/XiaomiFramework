package com.miui.common.card.functions;

public class FuncTopBannerScrollData {
    private String action;
    private String button;
    private BaseFunction commonFunction;
    private String icon;
    private String imgUrl;
    private String statKey;
    private String summary;
    private int template;
    private String title;

    public String getAction() {
        return this.action;
    }

    public String getButton() {
        return this.button;
    }

    public BaseFunction getCommonFunction() {
        return this.commonFunction;
    }

    public String getIcon() {
        return this.icon;
    }

    public String getImgUrl() {
        return this.imgUrl;
    }

    public String getStatKey() {
        return this.statKey;
    }

    public String getSummary() {
        return this.summary;
    }

    public int getTemplate() {
        return this.template;
    }

    public String getTitle() {
        return this.title;
    }

    public void setAction(String str) {
        this.action = str;
    }

    public void setButton(String str) {
        this.button = str;
    }

    public void setCommonFunction(BaseFunction baseFunction) {
        this.commonFunction = baseFunction;
    }

    public void setIcon(String str) {
        this.icon = str;
    }

    public void setImgUrl(String str) {
        this.imgUrl = str;
    }

    public void setStatKey(String str) {
        this.statKey = str;
    }

    public void setSummary(String str) {
        this.summary = str;
    }

    public void setTemplate(int i) {
        this.template = i;
    }

    public void setTitle(String str) {
        this.title = str;
    }
}
