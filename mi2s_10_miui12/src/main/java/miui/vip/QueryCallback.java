package miui.vip;

import java.util.List;

public class QueryCallback {
    long mMonitorTypes;

    public QueryCallback(int... types) {
        this.mMonitorTypes |= 8;
        this.mMonitorTypes |= 1;
        addMonitorTypes(types);
    }

    public void onUserInfo(int code, VipUserInfo user, String errMsg) {
    }

    public void onConnected(boolean serviceAvailable, VipUserInfo user, List<VipAchievement> list) {
    }

    public void onLevelByPhoneNumber(int code, List<VipPhoneLevel> list, String errMsg) {
    }

    public void onAchievements(int code, List<VipAchievement> list, String errMsg) {
    }

    public void onBanners(int code, List<VipBanner> list, String errMsg) {
    }

    public void addMonitorTypes(int... types) {
        if (types != null && types.length > 0) {
            int length = types.length;
            for (int i = 0; i < length; i++) {
                this.mMonitorTypes |= (long) types[i];
            }
        }
    }

    public void removeMonitorTypes(int... types) {
        if (types != null && types.length > 0) {
            int length = types.length;
            for (int i = 0; i < length; i++) {
                this.mMonitorTypes &= (long) (~types[i]);
            }
        }
    }

    public boolean isMonitorType(int type) {
        return (this.mMonitorTypes & ((long) type)) != 0;
    }
}
