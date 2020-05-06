package com.miui.net;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import b.b.c.j.y;
import com.miui.networkassistant.config.CommonConfig;
import com.miui.networkassistant.utils.NotificationUtil;
import java.util.Map;

public class MiuiNetworkSessionStats {
    private static final String TAG = "MiuiNetworkSessionStats";
    private CommonConfig mCommonConfig;
    private Context mContext;
    private miui.securitycenter.net.MiuiNetworkSessionStats mStatsCompat = new miui.securitycenter.net.MiuiNetworkSessionStats();

    public MiuiNetworkSessionStats(Context context) {
        this.mContext = context.getApplicationContext();
        this.mCommonConfig = CommonConfig.getInstance(this.mContext);
    }

    public void closeSession() {
        try {
            this.mStatsCompat.closeSession();
        } catch (Exception e) {
            Log.i(TAG, "closeSession", e);
        }
    }

    public void forceUpdate() {
        try {
            this.mStatsCompat.forceUpdate();
        } catch (Exception e) {
            Log.i(TAG, "forceUpdate", e);
        }
    }

    public long[] getMobileHistoryForUid(String str, int i, long j, long j2) {
        try {
            return this.mStatsCompat.getMobileHistoryForUid(str, i, j, j2);
        } catch (Exception e) {
            Log.i(TAG, "getMobileHistoryForUid", e);
            return null;
        }
    }

    public SparseArray<Map<String, Long>> getMobileSummaryForAllUid(String str, long j, long j2) {
        try {
            return this.mStatsCompat.getMobileSummaryForAllUid(str, j, j2);
        } catch (Exception e) {
            Log.i(TAG, "getMobileSummaryForAllUid", e);
            return null;
        }
    }

    public long getNetworkMobileTotalBytes(String str, long j, long j2) {
        try {
            return this.mStatsCompat.getNetworkMobileTotalBytes(str, j, j2);
        } catch (Exception e) {
            Log.i(TAG, "getNetworkMobileTotalBytes", e);
            return 0;
        }
    }

    public long getNetworkWifiTotalBytes(long j, long j2) {
        try {
            return this.mStatsCompat.getNetworkWifiTotalBytes(j, j2);
        } catch (Exception e) {
            Log.i(TAG, "getNetworkWifiTotalBytes", e);
            return 0;
        }
    }

    public long[] getWifiHistoryForUid(int i, long j, long j2) {
        try {
            return this.mStatsCompat.getWifiHistoryForUid(i, j, j2);
        } catch (Exception e) {
            Log.i(TAG, "getWifiHistoryForUid", e);
            return null;
        }
    }

    public SparseArray<Map<String, Long>> getWifiSummaryForAllUid(long j, long j2) {
        try {
            return this.mStatsCompat.getWifiSummaryForAllUid(j, j2);
        } catch (Exception e) {
            Log.i(TAG, "getWifiSummaryForAllUid", e);
            return null;
        }
    }

    public boolean isBandwidthModuleEnable() {
        return y.a("net.qtaguid_enabled", false);
    }

    public void openSession() {
        try {
            this.mStatsCompat.openSession();
        } catch (IllegalStateException e) {
            Log.i(TAG, "openSession IllegalStateException", e);
            long currentTimeMillis = System.currentTimeMillis();
            if (!isBandwidthModuleEnable() && currentTimeMillis - this.mCommonConfig.getNetworkExceptionUpdateTime() > 86400000) {
                this.mCommonConfig.setNetworkExceptionUpdateTime(currentTimeMillis);
                NotificationUtil.sendNetworkStatsExceptionNotify(this.mContext);
            }
        } catch (Exception e2) {
            Log.i(TAG, "openSession", e2);
        }
    }
}
