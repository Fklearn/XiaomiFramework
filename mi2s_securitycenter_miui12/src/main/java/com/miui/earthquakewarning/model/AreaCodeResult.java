package com.miui.earthquakewarning.model;

public class AreaCodeResult {
    private ChargeBean charge;
    private DataBean data;
    private boolean err;
    private String info;
    private int infocode;
    private String version;

    public static class ChargeBean {
        private boolean charge;
        private double price;

        public double getPrice() {
            return this.price;
        }

        public boolean isCharge() {
            return this.charge;
        }

        public void setCharge(boolean z) {
            this.charge = z;
        }

        public void setPrice(double d2) {
            this.price = d2;
        }
    }

    public static class DataBean {
        private String area;
        private String center;
        private String city;
        private int cityId;
        private String country;
        private String district;
        private int districtId;
        private String level;
        private String province;
        private String street;

        public String getArea() {
            return this.area;
        }

        public String getCenter() {
            return this.center;
        }

        public String getCity() {
            return this.city;
        }

        public int getCityId() {
            return this.cityId;
        }

        public String getCountry() {
            return this.country;
        }

        public String getDistrict() {
            return this.district;
        }

        public int getDistrictId() {
            return this.districtId;
        }

        public String getLevel() {
            return this.level;
        }

        public String getProvince() {
            return this.province;
        }

        public String getStreet() {
            return this.street;
        }

        public void setArea(String str) {
            this.area = str;
        }

        public void setCenter(String str) {
            this.center = str;
        }

        public void setCity(String str) {
            this.city = str;
        }

        public void setCityId(int i) {
            this.cityId = i;
        }

        public void setCountry(String str) {
            this.country = str;
        }

        public void setDistrict(String str) {
            this.district = str;
        }

        public void setDistrictId(int i) {
            this.districtId = i;
        }

        public void setLevel(String str) {
            this.level = str;
        }

        public void setProvince(String str) {
            this.province = str;
        }

        public void setStreet(String str) {
            this.street = str;
        }
    }

    public ChargeBean getCharge() {
        return this.charge;
    }

    public DataBean getData() {
        return this.data;
    }

    public String getInfo() {
        return this.info;
    }

    public int getInfocode() {
        return this.infocode;
    }

    public String getVersion() {
        return this.version;
    }

    public boolean isErr() {
        return this.err;
    }

    public void setCharge(ChargeBean chargeBean) {
        this.charge = chargeBean;
    }

    public void setData(DataBean dataBean) {
        this.data = dataBean;
    }

    public void setErr(boolean z) {
        this.err = z;
    }

    public void setInfo(String str) {
        this.info = str;
    }

    public void setInfocode(int i) {
        this.infocode = i;
    }

    public void setVersion(String str) {
        this.version = str;
    }
}
