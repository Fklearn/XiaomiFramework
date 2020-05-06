package com.android.server.notification;

import android.content.Context;

public class NotificationChannelExtractor implements NotificationSignalExtractor {
    private static final boolean DBG = false;
    private static final String TAG = "ChannelExtractor";
    private RankingConfig mConfig;

    public void initialize(Context ctx, NotificationUsageStats usageStats) {
    }

    public RankingReconsideration process(NotificationRecord record) {
        RankingConfig rankingConfig;
        if (record == null || record.getNotification() == null || (rankingConfig = this.mConfig) == null) {
            return null;
        }
        record.updateNotificationChannel(rankingConfig.getNotificationChannel(record.sbn.getPackageName(), record.sbn.getUid(), record.getChannel().getId(), false));
        return null;
    }

    public void setConfig(RankingConfig config) {
        this.mConfig = config;
    }

    public void setZenHelper(ZenModeHelper helper) {
    }
}
