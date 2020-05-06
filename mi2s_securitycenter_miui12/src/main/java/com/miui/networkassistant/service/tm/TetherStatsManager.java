package com.miui.networkassistant.service.tm;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import b.b.c.h.f;
import b.b.c.h.l;
import b.b.c.j.B;
import b.b.c.j.g;
import com.miui.networkassistant.config.CommonConfig;
import com.miui.networkassistant.ui.activity.TetherStatsOverLimitActivity;
import com.miui.networkassistant.utils.NotificationUtil;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import miui.securitycenter.NetworkUtils;

public class TetherStatsManager {
    private static final String TAG = "TetherStatsManager";
    private static final int TETHER_LIMIT_STOP_NETWORK = 0;
    private static final int TETHER_LIMIT_WARNING = 1;
    private CommonConfig mCommonConfig = CommonConfig.getInstance(this.mContext);
    private Context mContext;
    private boolean mIsWifiApEnabled;
    private boolean mTetheringLimitEnable;
    private long mTetheringLimitTraffic;
    private long mTetheringStartStats;

    public TetherStatsManager(Context context) {
        this.mContext = context;
    }

    private long getTetheringStats() {
        ArrayList networkStatsTethering = NetworkUtils.getNetworkStatsTethering();
        long j = 0;
        if (networkStatsTethering != null) {
            Iterator it = networkStatsTethering.iterator();
            while (it.hasNext()) {
                Map map = (Map) it.next();
                if (Integer.parseInt((String) map.get(MijiaAlertModel.KEY_UID)) == -5) {
                    j += Long.parseLong((String) map.get("rxBytes")) + Long.parseLong((String) map.get("txBytes"));
                }
            }
        }
        return j;
    }

    /* access modifiers changed from: package-private */
    public void checkTetheringTrafficStatus() {
        if (this.mIsWifiApEnabled && this.mTetheringLimitEnable && this.mTetheringLimitTraffic > 0 && getTetheringStats() - this.mTetheringStartStats > this.mTetheringLimitTraffic) {
            int tetheringOverLimitOptType = this.mCommonConfig.getTetheringOverLimitOptType();
            if (tetheringOverLimitOptType != 0) {
                if (tetheringOverLimitOptType == 1 && !this.mCommonConfig.getTetheringDataUsageOverLimit()) {
                    this.mCommonConfig.setTetheringDataUsageOverLimit(true);
                    NotificationUtil.sendTetherOverLimitWaringNotify(this.mContext);
                }
            } else if (!this.mCommonConfig.getTetheringDataUsageOverLimit()) {
                this.mCommonConfig.setTetheringDataUsageOverLimit(true);
                onTetherStatsOverLimit();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void initTetheringStatus() {
        initTetheringStatus(f.k(this.mContext));
    }

    /* access modifiers changed from: package-private */
    public void initTetheringStatus(boolean z) {
        this.mIsWifiApEnabled = z;
        Log.i(TAG, "Ap enable " + this.mIsWifiApEnabled);
        if (this.mIsWifiApEnabled) {
            this.mTetheringLimitEnable = this.mCommonConfig.getTetheringLimitEnabled();
            this.mTetheringLimitTraffic = this.mCommonConfig.getTetheringLimitTraffic();
            this.mTetheringStartStats = getTetheringStats();
            this.mCommonConfig.setTetheringDataUsageOverLimit(false);
        }
    }

    /* access modifiers changed from: package-private */
    public void onTetherStatsOverLimit() {
        l.a(this.mContext, false);
        Intent intent = new Intent();
        intent.setClass(this.mContext, TetherStatsOverLimitActivity.class);
        intent.addFlags(268435456);
        g.b(this.mContext, intent, B.b());
    }
}
