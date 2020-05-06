package com.miui.networkassistant.model;

public class TrafficInfo {
    public AppInfo mAppInfo;
    public AppStatistic mAppStats;

    public static class AppStatistic {
        public long[] mTotalBytes = new long[4];

        public AppStatistic() {
        }

        public AppStatistic(AppStatistic appStatistic) {
            for (int i = 0; i < 4; i++) {
                this.mTotalBytes[i] = appStatistic.mTotalBytes[i];
            }
        }

        public String toString() {
            return "yesterday total:" + this.mTotalBytes[0] + ",today total:" + this.mTotalBytes[1] + ",month total:" + this.mTotalBytes[2];
        }
    }

    public TrafficInfo(AppInfo appInfo) {
        this.mAppInfo = appInfo;
        this.mAppStats = new AppStatistic();
    }

    public TrafficInfo(TrafficInfo trafficInfo) {
        this.mAppInfo = trafficInfo.mAppInfo;
        this.mAppStats = new AppStatistic(trafficInfo.mAppStats);
    }
}
