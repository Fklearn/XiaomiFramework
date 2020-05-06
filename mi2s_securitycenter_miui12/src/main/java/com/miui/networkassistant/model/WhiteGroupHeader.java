package com.miui.networkassistant.model;

public class WhiteGroupHeader {
    private String mHeaderTitle;
    private WhiteGroupHeaderType mHeaderType;

    public enum WhiteGroupHeaderType {
        ENABLED,
        DISABLED
    }

    public WhiteGroupHeaderType getGroupHeaderType() {
        return this.mHeaderType;
    }

    public String getHeaderTitle() {
        return this.mHeaderTitle;
    }

    public void setGroupHeaderType(WhiteGroupHeaderType whiteGroupHeaderType) {
        this.mHeaderType = whiteGroupHeaderType;
    }

    public void setHeaderTitle(String str) {
        this.mHeaderTitle = str;
    }
}
