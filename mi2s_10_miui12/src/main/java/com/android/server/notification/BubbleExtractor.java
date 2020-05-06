package com.android.server.notification;

import android.content.Context;

public class BubbleExtractor implements NotificationSignalExtractor {
    private static final boolean DBG = false;
    private static final String TAG = "BubbleExtractor";
    private RankingConfig mConfig;

    public void initialize(Context ctx, NotificationUsageStats usageStats) {
    }

    public RankingReconsideration process(NotificationRecord record) {
        RankingConfig rankingConfig;
        if (record == null || record.getNotification() == null || (rankingConfig = this.mConfig) == null) {
            return null;
        }
        boolean userWantsBubbles = rankingConfig.bubblesEnabled(record.sbn.getUser());
        boolean appCanShowBubble = this.mConfig.areBubblesAllowed(record.sbn.getPackageName(), record.sbn.getUid());
        boolean z = false;
        if (!userWantsBubbles || !appCanShowBubble) {
            record.setAllowBubble(false);
        } else if (record.getChannel() != null) {
            if (record.getChannel().canBubble() && appCanShowBubble) {
                z = true;
            }
            record.setAllowBubble(z);
        } else {
            record.setAllowBubble(appCanShowBubble);
        }
        return null;
    }

    public void setConfig(RankingConfig config) {
        this.mConfig = config;
    }

    public void setZenHelper(ZenModeHelper helper) {
    }
}
