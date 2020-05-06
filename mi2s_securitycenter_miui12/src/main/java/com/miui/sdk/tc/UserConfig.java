package com.miui.sdk.tc;

public class UserConfig {
    private String mCityId;
    private String mOperatorId;
    private String mProvinceId;

    public UserConfig() {
    }

    public UserConfig(String str, String str2, String str3) {
        this.mProvinceId = str;
        this.mCityId = str2;
        this.mOperatorId = str3;
    }

    public String getCity() {
        return this.mCityId;
    }

    public String getOperator() {
        return this.mOperatorId;
    }

    public String getProvince() {
        return this.mProvinceId;
    }

    public void setCity(String str) {
        this.mCityId = str;
    }

    public void setOperator(String str) {
        this.mOperatorId = str;
    }

    public void setProvince(String str) {
        this.mProvinceId = str;
    }
}
