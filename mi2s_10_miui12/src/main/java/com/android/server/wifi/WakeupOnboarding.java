package com.android.server.wifi;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.wifi.WakeupConfigStoreData;
import miui.app.constants.ThemeManagerConstants;

public class WakeupOnboarding {
    @VisibleForTesting
    static final int NOTIFICATIONS_UNTIL_ONBOARDED = 3;
    private static final long NOT_SHOWN_TIMESTAMP = -1;
    @VisibleForTesting
    static final long REQUIRED_NOTIFICATION_DELAY = 86400000;
    private static final String TAG = "WakeupOnboarding";
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        /* JADX WARNING: Removed duplicated region for block: B:17:0x003c  */
        /* JADX WARNING: Removed duplicated region for block: B:22:0x008d  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r7, android.content.Intent r8) {
            /*
                r6 = this;
                java.lang.String r0 = r8.getAction()
                int r1 = r0.hashCode()
                r2 = -1067607823(0xffffffffc05d98f1, float:-3.4624598)
                r3 = 0
                r4 = 2
                r5 = 1
                if (r1 == r2) goto L_0x002f
                r2 = -506616242(0xffffffffe1cda64e, float:-4.7419576E20)
                if (r1 == r2) goto L_0x0025
                r2 = 1771495157(0x6996def5, float:2.279897E25)
                if (r1 == r2) goto L_0x001b
            L_0x001a:
                goto L_0x0039
            L_0x001b:
                java.lang.String r1 = "com.android.server.wifi.wakeup.DISMISS_NOTIFICATION"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x001a
                r0 = r4
                goto L_0x003a
            L_0x0025:
                java.lang.String r1 = "com.android.server.wifi.wakeup.OPEN_WIFI_PREFERENCES"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x001a
                r0 = r5
                goto L_0x003a
            L_0x002f:
                java.lang.String r1 = "com.android.server.wifi.wakeup.TURN_OFF_WIFI_WAKE"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x001a
                r0 = r3
                goto L_0x003a
            L_0x0039:
                r0 = -1
            L_0x003a:
                if (r0 == 0) goto L_0x008d
                if (r0 == r5) goto L_0x0061
                if (r0 == r4) goto L_0x005b
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r1 = "Unknown action "
                r0.append(r1)
                java.lang.String r1 = r8.getAction()
                r0.append(r1)
                java.lang.String r0 = r0.toString()
                java.lang.String r1 = "WakeupOnboarding"
                android.util.Log.e(r1, r0)
                goto L_0x00a4
            L_0x005b:
                com.android.server.wifi.WakeupOnboarding r0 = com.android.server.wifi.WakeupOnboarding.this
                r0.dismissNotification(r5)
                goto L_0x00a4
            L_0x0061:
                com.android.server.wifi.WakeupOnboarding r0 = com.android.server.wifi.WakeupOnboarding.this
                android.content.Context r0 = r0.mContext
                android.content.Intent r1 = new android.content.Intent
                java.lang.String r2 = "android.intent.action.CLOSE_SYSTEM_DIALOGS"
                r1.<init>(r2)
                r0.sendBroadcast(r1)
                com.android.server.wifi.WakeupOnboarding r0 = com.android.server.wifi.WakeupOnboarding.this
                android.content.Context r0 = r0.mContext
                android.content.Intent r1 = new android.content.Intent
                java.lang.String r2 = "android.settings.WIFI_IP_SETTINGS"
                r1.<init>(r2)
                r2 = 268435456(0x10000000, float:2.5243549E-29)
                android.content.Intent r1 = r1.addFlags(r2)
                r0.startActivity(r1)
                com.android.server.wifi.WakeupOnboarding r0 = com.android.server.wifi.WakeupOnboarding.this
                r0.dismissNotification(r5)
                goto L_0x00a4
            L_0x008d:
                com.android.server.wifi.WakeupOnboarding r0 = com.android.server.wifi.WakeupOnboarding.this
                com.android.server.wifi.FrameworkFacade r0 = r0.mFrameworkFacade
                com.android.server.wifi.WakeupOnboarding r1 = com.android.server.wifi.WakeupOnboarding.this
                android.content.Context r1 = r1.mContext
                java.lang.String r2 = "wifi_wakeup_enabled"
                r0.setIntegerSetting(r1, r2, r3)
                com.android.server.wifi.WakeupOnboarding r0 = com.android.server.wifi.WakeupOnboarding.this
                r0.dismissNotification(r5)
            L_0x00a4:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WakeupOnboarding.AnonymousClass1.onReceive(android.content.Context, android.content.Intent):void");
        }
    };
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public final FrameworkFacade mFrameworkFacade;
    private final Handler mHandler;
    private final IntentFilter mIntentFilter;
    private boolean mIsNotificationShowing;
    /* access modifiers changed from: private */
    public boolean mIsOnboarded;
    private long mLastShownTimestamp = -1;
    private NotificationManager mNotificationManager;
    /* access modifiers changed from: private */
    public int mTotalNotificationsShown;
    private final WakeupNotificationFactory mWakeupNotificationFactory;
    private final WifiConfigManager mWifiConfigManager;

    public WakeupOnboarding(Context context, WifiConfigManager wifiConfigManager, Looper looper, FrameworkFacade frameworkFacade, WakeupNotificationFactory wakeupNotificationFactory) {
        this.mContext = context;
        this.mWifiConfigManager = wifiConfigManager;
        this.mHandler = new Handler(looper);
        this.mFrameworkFacade = frameworkFacade;
        this.mWakeupNotificationFactory = wakeupNotificationFactory;
        this.mIntentFilter = new IntentFilter();
        this.mIntentFilter.addAction(WakeupNotificationFactory.ACTION_TURN_OFF_WIFI_WAKE);
        this.mIntentFilter.addAction(WakeupNotificationFactory.ACTION_DISMISS_NOTIFICATION);
        this.mIntentFilter.addAction(WakeupNotificationFactory.ACTION_OPEN_WIFI_PREFERENCES);
    }

    public boolean isOnboarded() {
        return this.mIsOnboarded;
    }

    public void maybeShowNotification() {
        maybeShowNotification(SystemClock.elapsedRealtime());
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void maybeShowNotification(long timestamp) {
        if (shouldShowNotification(timestamp)) {
            Log.d(TAG, "Showing onboarding notification.");
            incrementTotalNotificationsShown();
            this.mIsNotificationShowing = true;
            this.mLastShownTimestamp = timestamp;
            this.mContext.registerReceiver(this.mBroadcastReceiver, this.mIntentFilter, (String) null, this.mHandler);
            getNotificationManager().notify(43, this.mWakeupNotificationFactory.createOnboardingNotification());
        }
    }

    private void incrementTotalNotificationsShown() {
        this.mTotalNotificationsShown++;
        if (this.mTotalNotificationsShown >= 3) {
            setOnboarded();
        } else {
            this.mWifiConfigManager.saveToStore(false);
        }
    }

    private boolean shouldShowNotification(long timestamp) {
        if (isOnboarded() || this.mIsNotificationShowing) {
            return false;
        }
        long j = this.mLastShownTimestamp;
        if (j == -1 || timestamp - j > 86400000) {
            return true;
        }
        return false;
    }

    public void onStop() {
        dismissNotification(false);
    }

    /* access modifiers changed from: private */
    public void dismissNotification(boolean shouldOnboard) {
        if (this.mIsNotificationShowing) {
            if (shouldOnboard) {
                setOnboarded();
            }
            this.mContext.unregisterReceiver(this.mBroadcastReceiver);
            getNotificationManager().cancel(43);
            this.mIsNotificationShowing = false;
        }
    }

    public void setOnboarded() {
        if (!this.mIsOnboarded) {
            Log.d(TAG, "Setting user as onboarded.");
            this.mIsOnboarded = true;
            this.mWifiConfigManager.saveToStore(false);
        }
    }

    private NotificationManager getNotificationManager() {
        if (this.mNotificationManager == null) {
            this.mNotificationManager = (NotificationManager) this.mContext.getSystemService(ThemeManagerConstants.COMPONENT_CODE_NOTIFICATION);
        }
        return this.mNotificationManager;
    }

    public WakeupConfigStoreData.DataSource<Boolean> getIsOnboadedDataSource() {
        return new IsOnboardedDataSource();
    }

    public WakeupConfigStoreData.DataSource<Integer> getNotificationsDataSource() {
        return new NotificationsDataSource();
    }

    private class IsOnboardedDataSource implements WakeupConfigStoreData.DataSource<Boolean> {
        private IsOnboardedDataSource() {
        }

        public Boolean getData() {
            return Boolean.valueOf(WakeupOnboarding.this.mIsOnboarded);
        }

        public void setData(Boolean data) {
            boolean unused = WakeupOnboarding.this.mIsOnboarded = data.booleanValue();
        }
    }

    private class NotificationsDataSource implements WakeupConfigStoreData.DataSource<Integer> {
        private NotificationsDataSource() {
        }

        public Integer getData() {
            return Integer.valueOf(WakeupOnboarding.this.mTotalNotificationsShown);
        }

        public void setData(Integer data) {
            int unused = WakeupOnboarding.this.mTotalNotificationsShown = data.intValue();
        }
    }
}
