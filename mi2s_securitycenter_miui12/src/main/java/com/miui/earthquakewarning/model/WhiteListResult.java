package com.miui.earthquakewarning.model;

public class WhiteListResult {
    private int code;
    private DataBean data;
    private String desc;

    public static class DataBean {
        private boolean checkResult;

        public boolean isCheckResult() {
            return this.checkResult;
        }

        public void setCheckResult(boolean z) {
            this.checkResult = z;
        }
    }

    public int getCode() {
        return this.code;
    }

    public DataBean getData() {
        return this.data;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setCode(int i) {
        this.code = i;
    }

    public void setData(DataBean dataBean) {
        this.data = dataBean;
    }

    public void setDesc(String str) {
        this.desc = str;
    }
}
