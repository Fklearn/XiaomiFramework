package com.miui.networkassistant.service.tm;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import b.b.c.c.a.a;
import com.miui.networkassistant.config.CommonConfig;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.dual.Sim;
import com.miui.networkassistant.model.AppInfo;
import com.miui.networkassistant.service.wrapper.AppMonitorWrapper;
import com.miui.networkassistant.traffic.lockscreen.LockScreenTrafficHelper;
import com.miui.networkassistant.traffic.statistic.NaTrafficStats;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.NotificationUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class LockScreenManager {
    private static final long LOCK_SCREEN_APP_MIN = 1024;
    private static final int LOCK_SCREEN_GUIDE_MAX_COUNT = 1;
    private static final long LOCK_SCREEN_WARNING_TIME_MAX = 172800000;
    private static final long LOCK_SCREEN_WARNING_TIME_MIN = 0;
    private static final String TAG = "LockScreenManager";
    private CommonConfig mConfig;
    private HashMap<Integer, Long> mCurrentUidMapSelfLocked = new HashMap<>();
    private long mLockScreenBeginTime;
    private long mLockScreenEndTime;
    private boolean mLockScreenMonitorEnabled = false;
    private AppMonitorWrapper mMonitorCenter;
    private TrafficManageService mService;
    private SimUserInfo mSimUserInfo;
    private TrafficSimManager[] mTrafficManagers;
    private long mWarningBytesLimit = 102400;

    LockScreenManager(TrafficManageService trafficManageService, TrafficSimManager[] trafficSimManagerArr) {
        this.mService = trafficManageService;
        this.mTrafficManagers = trafficSimManagerArr;
        this.mConfig = CommonConfig.getInstance(this.mService);
        this.mLockScreenBeginTime = System.currentTimeMillis();
        initLockScreenMonitor();
    }

    private void checkAndGetMonitorCenter() {
        if (this.mMonitorCenter == null) {
            this.mMonitorCenter = AppMonitorWrapper.getInstance(this.mService);
        }
    }

    private void initLockScreenDataUpgrade() {
        boolean isLockScreenTrafficMonitorEnable = this.mConfig.isLockScreenTrafficMonitorEnable();
        if (isLockScreenTrafficMonitorEnable) {
            this.mTrafficManagers[0].mSimUser.setLockScreenTrafficEnable(isLockScreenTrafficMonitorEnable);
            if (DeviceUtil.IS_DUAL_CARD) {
                this.mTrafficManagers[1].mSimUser.setLockScreenTrafficEnable(isLockScreenTrafficMonitorEnable);
            }
            this.mConfig.setLockScreenTrafficMonitorEnable(false);
        }
    }

    /* access modifiers changed from: private */
    public void markAndCalculate(String str) {
        synchronized (this.mCurrentUidMapSelfLocked) {
            checkAndGetMonitorCenter();
            if (Constants.System.ACTION_USER_PRESENT.equals(str)) {
                this.mLockScreenEndTime = System.currentTimeMillis();
                long j = this.mLockScreenEndTime - this.mLockScreenBeginTime;
                if (j > 0 && j < 172800000) {
                    int i = 0;
                    ArrayList<AppInfo> filteredAppInfosList = this.mMonitorCenter.getFilteredAppInfosList();
                    if (filteredAppInfosList != null) {
                        Iterator<AppInfo> it = filteredAppInfosList.iterator();
                        while (it.hasNext()) {
                            AppInfo next = it.next();
                            Long l = this.mCurrentUidMapSelfLocked.get(Integer.valueOf(next.uid));
                            if (l != null) {
                                long longValue = l.longValue();
                                if (longValue >= 0) {
                                    long mobileBytes = NaTrafficStats.getMobileBytes(this.mService.getApplicationContext(), next.uid) - longValue;
                                    if (mobileBytes >= 1024) {
                                        this.mCurrentUidMapSelfLocked.put(Integer.valueOf(next.uid), Long.valueOf(mobileBytes));
                                        i = (int) (((long) i) + mobileBytes);
                                    } else {
                                        this.mCurrentUidMapSelfLocked.remove(Integer.valueOf(next.uid));
                                    }
                                }
                            }
                        }
                    }
                    long j2 = (long) i;
                    if (j2 > this.mWarningBytesLimit) {
                        if (this.mLockScreenMonitorEnabled) {
                            NotificationUtil.sendLockScreenTrafficUsed(this.mService, j2, j, this.mLockScreenBeginTime, this.mCurrentUidMapSelfLocked);
                        } else if (!this.mSimUserInfo.isNotLimitCardEnable()) {
                            sendAndCheckLockScreenTrafficGuide(this.mService, j2);
                        }
                    }
                }
            } else if (Constants.System.ACTION_SCREEN_OFF.equals(str)) {
                this.mLockScreenBeginTime = System.currentTimeMillis();
                this.mCurrentUidMapSelfLocked.clear();
                ArrayList<AppInfo> filteredAppInfosList2 = this.mMonitorCenter.getFilteredAppInfosList();
                if (filteredAppInfosList2 != null) {
                    Iterator<AppInfo> it2 = filteredAppInfosList2.iterator();
                    while (it2.hasNext()) {
                        AppInfo next2 = it2.next();
                        this.mCurrentUidMapSelfLocked.put(Integer.valueOf(next2.uid), Long.valueOf(NaTrafficStats.getMobileBytes(this.mService.getApplicationContext(), next2.uid)));
                    }
                }
            }
        }
    }

    private void sendAndCheckLockScreenTrafficGuide(Context context, long j) {
        System.currentTimeMillis();
        int lockScreenTrafficGuideNotifyCount = this.mConfig.getLockScreenTrafficGuideNotifyCount();
        if (!this.mConfig.isLockScreenTrafficOpened() && lockScreenTrafficGuideNotifyCount < 1) {
            NotificationUtil.sendLockScreenTrafficGuideNotify(context, j);
            this.mConfig.setLockScreenTrafficGuideNotifyCount(lockScreenTrafficGuideNotifyCount + 1);
        }
    }

    /* access modifiers changed from: package-private */
    public void initLockScreenMonitor() {
        int currentActiveSlotNum = Sim.getCurrentActiveSlotNum();
        TrafficSimManager[] trafficSimManagerArr = this.mTrafficManagers;
        if (trafficSimManagerArr[currentActiveSlotNum] != null) {
            this.mSimUserInfo = trafficSimManagerArr[currentActiveSlotNum].mSimUser;
            SimUserInfo simUserInfo = this.mSimUserInfo;
            if (simUserInfo != null && simUserInfo.hasImsi()) {
                initLockScreenDataUpgrade();
                this.mLockScreenMonitorEnabled = this.mSimUserInfo.isLockScreenTrafficEnable() && !this.mSimUserInfo.isNotLimitCardEnable();
                this.mWarningBytesLimit = LockScreenTrafficHelper.getWarningLimitBytes(this.mSimUserInfo.getLockScreenWarningLevel(), this.mTrafficManagers[currentActiveSlotNum].getCurrentMonthTotalPackage());
                if (this.mLockScreenMonitorEnabled) {
                    this.mConfig.setLockScreenTrafficOpened(true);
                }
                Log.i(TAG, String.format("[LockScreenManager] mLockScreenMonitorEnabled: %s, mWarningBytesLimit: %s, getBrand: %s", new Object[]{Boolean.valueOf(this.mLockScreenMonitorEnabled), Long.valueOf(this.mWarningBytesLimit), Integer.valueOf(this.mSimUserInfo.getBrand())}));
                checkAndGetMonitorCenter();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void onLockScreenChange(Intent intent) {
        SimUserInfo simUserInfo = this.mSimUserInfo;
        if (simUserInfo != null && simUserInfo.isExistTotalDataUsage()) {
            final String action = intent.getAction();
            a.a(new Runnable() {
                public void run() {
                    LockScreenManager.this.markAndCalculate(action);
                }
            });
        }
    }
}
