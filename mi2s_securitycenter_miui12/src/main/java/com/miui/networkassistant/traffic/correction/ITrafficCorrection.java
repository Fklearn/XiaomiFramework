package com.miui.networkassistant.traffic.correction;

import com.miui.networkassistant.model.TrafficUsedStatus;
import java.util.Map;

public interface ITrafficCorrection {
    public static final String KEY_CORRECTION_TYPE = "correction_type";
    public static final int TC_TYPE_ALL = 7;
    public static final int TC_TYPE_BILL = 2;
    public static final int TC_TYPE_CALLTIME = 4;
    public static final int TC_TYPE_TRAFFIC = 1;
    public static final long TIMEOUT_MILLION = 540000;

    public static class TrafficConfig {
        private String mBrandId;
        private String mCityId;
        private String mImsi;
        private String mOperatorId;
        private String mProvinceId;

        public TrafficConfig(String str, String str2, String str3) {
            this.mProvinceId = str;
            this.mCityId = str2;
            this.mBrandId = str3;
        }

        public TrafficConfig(String str, String str2, String str3, String str4, String str5) {
            this.mImsi = str;
            this.mProvinceId = str2;
            this.mCityId = str3;
            this.mOperatorId = str4;
            this.mBrandId = str5;
        }

        public String getBrandId() {
            return this.mBrandId;
        }

        public String getCityId() {
            return this.mCityId;
        }

        public String getImsi() {
            return this.mImsi;
        }

        public String getOperatorId() {
            return this.mOperatorId;
        }

        public String getProvinceId() {
            return this.mProvinceId;
        }

        public void setBrandId(String str) {
            this.mBrandId = str;
        }

        public void setCityId(String str) {
            this.mCityId = str;
        }

        public void setImsi(String str) {
            this.mImsi = str;
        }

        public void setOperatorId(String str) {
            this.mOperatorId = str;
        }

        public void setProvinceId(String str) {
            this.mProvinceId = str;
        }
    }

    public interface TrafficCorrectionListener {
        void onTrafficCorrected(TrafficUsedStatus trafficUsedStatus);
    }

    Map<String, String> getBrands(String str);

    Map<Integer, String> getCities(int i);

    TrafficConfig getConfig();

    Map<String, String> getInstructions(int i);

    Map<String, String> getOperators();

    int getProvinceCodeByCityCode(int i);

    Map<Integer, String> getProvinces();

    int getTcType();

    boolean isConfigUpdated();

    boolean isFinished();

    void registerLisener(TrafficCorrectionListener trafficCorrectionListener);

    boolean saveConfig(TrafficConfig trafficConfig);

    void setTotalLimit(long j);

    boolean startCorrection(boolean z, Map<String, String> map);

    boolean startCorrection(boolean z, Map<String, String> map, long j, int i);

    void unRegisterLisener(TrafficCorrectionListener trafficCorrectionListener);

    boolean updateSMSTemplate(String str, String str2, String str3);
}
