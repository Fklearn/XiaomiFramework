package com.miui.earthquakewarning.model;

import java.util.List;

public class SignatureReuslt {
    private int code;
    private List<DatasBean> datas;
    private String desc;

    public static class DataBean {
        private int code;
        private String district;
        private List<String> signs;

        public int getCode() {
            return this.code;
        }

        public String getDistrict() {
            return this.district;
        }

        public List<String> getSigns() {
            return this.signs;
        }

        public void setCode(int i) {
            this.code = i;
        }

        public void setDistrict(String str) {
            this.district = str;
        }

        public void setSigns(List<String> list) {
            this.signs = list;
        }
    }

    public static class DatasBean {
        private String channel;
        private List<DataBean> data;

        public String getChannel() {
            return this.channel;
        }

        public List<DataBean> getData() {
            return this.data;
        }

        public void setChannel(String str) {
            this.channel = str;
        }

        public void setData(List<DataBean> list) {
            this.data = list;
        }
    }

    public int getCode() {
        return this.code;
    }

    public List<DatasBean> getDatas() {
        return this.datas;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setCode(int i) {
        this.code = i;
    }

    public void setDatas(List<DatasBean> list) {
        this.datas = list;
    }

    public void setDesc(String str) {
        this.desc = str;
    }
}
