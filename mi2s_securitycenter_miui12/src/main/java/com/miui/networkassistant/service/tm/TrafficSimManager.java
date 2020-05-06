package com.miui.networkassistant.service.tm;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.c.a.a;
import b.b.c.h.f;
import b.b.c.j.B;
import b.b.c.j.g;
import b.b.o.g.b;
import com.miui.net.MiuiNetworkSessionStats;
import com.miui.networkassistant.config.CommonConfig;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.config.DataUsageIgnoreAppListConfig;
import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.dual.Sim;
import com.miui.networkassistant.model.TrafficUsedStatus;
import com.miui.networkassistant.provider.ProviderConstant;
import com.miui.networkassistant.traffic.correction.ITrafficCorrection;
import com.miui.networkassistant.traffic.correction.TrafficCorrectionManager;
import com.miui.networkassistant.traffic.purchase.CooperationManager;
import com.miui.networkassistant.traffic.statistic.MiSimHelper;
import com.miui.networkassistant.ui.activity.NetworkOverLimitActivity;
import com.miui.networkassistant.ui.activity.TrafficConfigAlertActivity;
import com.miui.networkassistant.utils.AnalyticsHelper;
import com.miui.networkassistant.utils.DateUtil;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.FormatBytesUtil;
import com.miui.networkassistant.utils.LoadConfigUtil;
import com.miui.networkassistant.utils.MiSimUtil;
import com.miui.networkassistant.utils.NotificationUtil;
import com.miui.networkassistant.utils.PackageUtil;
import com.miui.networkassistant.utils.PrivacyDeclareAndAllowNetworkUtil;
import com.miui.networkassistant.utils.TelephonyUtil;
import com.miui.networkassistant.utils.VirtualSimUtil;
import com.miui.networkassistant.webapi.DataUsageResult;
import com.miui.networkassistant.webapi.WebApiAccessHelper;
import com.miui.permission.PermissionManager;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import miui.util.Log;
import org.json.JSONArray;
import org.json.JSONException;

public class TrafficSimManager {
    private static final int MEGA = 1048576;
    private static final int OVER_DAILY_LIMIT_STOP_NETWORK = 1;
    private static final int OVER_DAILY_LIMIT_WARNING = 0;
    private static final String TAG = "TrafficManageService";
    private static HashMap<String, TrafficSimManager> sInstanceMap = new HashMap<>();
    private long mCacheLeisureTime = 0;
    private long mCacheLeisureUsed = 0;
    private CommonConfig mCommonConfig;
    /* access modifiers changed from: private */
    public Context mContext;
    private String mCurrentImsi;
    private boolean mDailyCardEnable;
    private long mDailyCardPackage;
    private long mDailyLimitTraffic;
    private HashSet<Integer> mDataUsageIgnoreUidListSelfLocked = new HashSet<>();
    private long mDataUsageTotalPackage;
    private long mDataUsageTotalPackageWarning;
    DataUsageIgnoreAppListConfig mIgnoreAppListConfig;
    private boolean mIsDailyLimitEnabled;
    private boolean mIsDataUsageOverLimitOn;
    private boolean mIsDataUsageOverNormalPkg;
    private boolean mIsLeisureDataUsageOverLimitOn;
    private boolean mIsMiSim;
    private boolean mIsRoamingDailyLimitEnabled;
    /* access modifiers changed from: private */
    public boolean mIsSimLocationError = false;
    /* access modifiers changed from: private */
    public boolean mIsTcDiagnostic = false;
    private boolean mIsTotalPackageSetted;
    private boolean mIsTrafficPurchaseAvailable;
    /* access modifiers changed from: private */
    public boolean mIsUserCorrection;
    private long mLeisureDataUsageTotal;
    private long mLeisureFromTime;
    private long mLeisureToTime;
    private MiSimHelper mMiSimHelper;
    /* access modifiers changed from: private */
    public MiuiNetworkSessionStats mMiuiNetworkSessionStats;
    private long mMonthStartTime;
    private int mOverDailyLimitWarningType;
    private long mRoamingDailyLimitTraffic;
    private int mRoamingOverLimitOptType;
    /* access modifiers changed from: private */
    public TrafficManageService mService;
    /* access modifiers changed from: private */
    public boolean mShouldUpdateTcEngine = false;
    SimUserInfo mSimUser;
    ITrafficCorrection mTrafficCorrection;
    private ITrafficCorrection.TrafficCorrectionListener mTrafficCorrectionListener = new ITrafficCorrection.TrafficCorrectionListener() {
        public void onTrafficCorrected(TrafficUsedStatus trafficUsedStatus) {
            if (!TrafficSimManager.this.mIsTcDiagnostic) {
                TrafficSimManager.this.saveAnalytics(trafficUsedStatus);
                TrafficSimManager.this.saveBillAndCallTimeResult(trafficUsedStatus);
                TrafficSimManager.this.checkBillRemainder(trafficUsedStatus);
                int returnCode = trafficUsedStatus.getReturnCode();
                if (returnCode != 10) {
                    switch (returnCode) {
                        case 0:
                            if (trafficUsedStatus.getCorrectionType() == 2) {
                                TrafficSimManager.this.notifyBillPackageChange();
                            } else if (trafficUsedStatus.getCorrectionType() == 1) {
                                TrafficSimManager.this.notifyTrafficPackageChange();
                            }
                            if (!TrafficSimManager.this.checkPackagesConfig(trafficUsedStatus) && !TrafficSimManager.this.checkTotalLimitError(trafficUsedStatus)) {
                                TrafficSimManager.this.saveCorrectedPkgsAndUsageValues(trafficUsedStatus, false);
                                break;
                            }
                        case 1:
                        case 2:
                        case 4:
                        case 5:
                            break;
                        case 3:
                            TrafficSimManager trafficSimManager = TrafficSimManager.this;
                            trafficSimManager.showDataUsageCorrectionTimeOutNotify(trafficSimManager.mContext, TrafficSimManager.this.mSimUser.getSlotNum());
                            break;
                        case 6:
                            TrafficSimManager.this.showCorrectionFailedToast();
                            break;
                    }
                }
                boolean unused = TrafficSimManager.this.mShouldUpdateTcEngine = true;
                if (!TrafficSimManager.this.mIsSimLocationError) {
                    TrafficSimManager trafficSimManager2 = TrafficSimManager.this;
                    trafficSimManager2.showDataUsageCorrectionFailureNotify(trafficSimManager2.mContext, TrafficSimManager.this.mSimUser.getSlotNum());
                }
                TrafficSimManager.this.finishCorrection(false);
                TrafficSimManager.this.shouldUpdateTcEngine(trafficUsedStatus);
                TrafficSimManager.this.saveCorrectionResult(trafficUsedStatus);
            }
        }
    };
    private Object mUpdateAutoCorrectionLock = new Object();

    private TrafficSimManager(TrafficManageService trafficManageService, String str) {
        this.mService = trafficManageService;
        this.mContext = trafficManageService.getApplicationContext();
        this.mCurrentImsi = str;
        this.mCommonConfig = CommonConfig.getInstance(this.mContext);
        this.mMiSimHelper = new MiSimHelper(this.mContext);
    }

    /* access modifiers changed from: private */
    public void checkBillRemainder(TrafficUsedStatus trafficUsedStatus) {
        Intent billIntent = VirtualSimUtil.getBillIntent(this.mSimUser.getSlotNum());
        if (trafficUsedStatus.getCorrectionType() == 2 && trafficUsedStatus.getReturnCode() == 0 && trafficUsedStatus.getBillRemained() < 1000 && PackageUtil.isIntentExist(this.mContext, billIntent) && this.mSimUser.getLastBillNotifyTime() < DateUtil.getThisMonthBeginTimeMillis(1)) {
            NotificationUtil.sendBillWarningNotify(this.mContext, String.valueOf(((float) trafficUsedStatus.getBillRemained()) / 100.0f), this.mSimUser.getSlotNum(), billIntent);
            this.mSimUser.setLastBillNotifyTime(System.currentTimeMillis());
        }
    }

    private int checkCorrectionType(int i) {
        if (this.mSimUser.isDailyUsedCardEnable() || this.mSimUser.isNotLimitCardEnable()) {
            return 2;
        }
        return i;
    }

    private void checkDailyUsedTrafficStatus() {
        long correctedMonthTotalUsed = getCorrectedMonthTotalUsed();
        long normalMonthTotalPackage = getNormalMonthTotalPackage();
        this.mMonthStartTime = DateUtil.getThisMonthBeginTimeMillis(this.mSimUser.getMonthStart());
        long currentTimeMillis = System.currentTimeMillis();
        this.mIsDataUsageOverNormalPkg = correctedMonthTotalUsed - normalMonthTotalPackage >= 0;
        if (this.mIsDataUsageOverNormalPkg) {
            int dailyUsedCardStopNetworkCount = this.mSimUser.getDailyUsedCardStopNetworkCount() + 1;
            long j = ((long) dailyUsedCardStopNetworkCount) * this.mDailyCardPackage;
            if (((float) getTodayCorrectDataUsageUsed()) >= ((float) j) - (((float) this.mDailyCardPackage) * 0.100000024f) && this.mSimUser.getDailyUsedCardStopNetworkTime() < DateUtil.getTodayTimeMillis()) {
                this.mSimUser.setDailyUsedCardStopNetworkTime(currentTimeMillis);
                this.mSimUser.setDailyUsedCardDataUpdateTime(currentTimeMillis);
                if (this.mSimUser.getDailyUsedCardStopNetworkOn()) {
                    this.mSimUser.setOverDataUsageStopNetworkType(4);
                    onNormalTrafficOverLimit();
                    NotificationUtil.cancelDataUsageOverLimit(this.mContext);
                    this.mSimUser.setMobilePolicyEnable(false);
                    return;
                }
                NotificationUtil.sendDailyCardDataUsageOverLimit(this.mContext, dailyUsedCardStopNetworkCount);
                this.mSimUser.setDailyUsedCardStopNetworkCount(dailyUsedCardStopNetworkCount);
                this.mSimUser.setDailyUsedCardStopNetworkTime(0);
            }
        } else if (normalMonthTotalPackage > 0) {
            checkNormalTrafficStatus(normalMonthTotalPackage, correctedMonthTotalUsed, currentTimeMillis);
        }
    }

    private void checkLeisureTrafficStatus(long j, long j2) {
        if (j < this.mLeisureDataUsageTotal) {
            return;
        }
        if (this.mIsLeisureDataUsageOverLimitOn) {
            if (j2 > this.mSimUser.getLeisureDataUsageOverLimitWarningTime() + 86400000) {
                this.mSimUser.saveLeisureDataUsageOverLimitWarningTime(j2);
                NotificationUtil.sendLeisureDataUsageWarning(this.mContext);
            }
        } else if (this.mSimUser.getLeisureOverLimitStopNetworkTime() < this.mMonthStartTime) {
            this.mSimUser.saveLeisureOverLimitStopNetworkTime(j2);
            this.mSimUser.setOverDataUsageStopNetworkType(3);
            onNormalTrafficOverLimit();
            this.mSimUser.setMobilePolicyEnable(false);
        }
    }

    private void checkNormalAndLeisureTrafficStatus() {
        Log.i(TAG, "mina checkNormalAndLeisureTrafficStatus ");
        this.mMonthStartTime = DateUtil.getThisMonthBeginTimeMillis(this.mSimUser.getMonthStart());
        long[] correctedNormalAndLeisureMonthTotalUsed = getCorrectedNormalAndLeisureMonthTotalUsed();
        long currentTimeMillis = System.currentTimeMillis();
        checkNormalTrafficStatus(this.mDataUsageTotalPackage, correctedNormalAndLeisureMonthTotalUsed[0], currentTimeMillis);
        checkLeisureTrafficStatus(correctedNormalAndLeisureMonthTotalUsed[1], currentTimeMillis);
    }

    private void checkNormalTrafficStatus() {
        Log.i(TAG, "mina checkNormalTrafficStatus ");
        long correctedNormalMonthTotalUsed = getCorrectedNormalMonthTotalUsed();
        this.mMonthStartTime = DateUtil.getThisMonthBeginTimeMillis(this.mSimUser.getMonthStart());
        checkNormalTrafficStatus(this.mDataUsageTotalPackage, correctedNormalMonthTotalUsed, System.currentTimeMillis());
    }

    private void checkNormalTrafficStatus(long j, long j2, long j3) {
        boolean z = false;
        if (j2 >= j) {
            if (this.mSimUser.getDataUsageOverLimitStopNetworkTime() < this.mMonthStartTime) {
                this.mSimUser.saveDataUsageOverLimitStopNetworkTime(j3);
                if (this.mIsDataUsageOverLimitOn) {
                    this.mSimUser.setOverDataUsageStopNetworkType(0);
                    onNormalTrafficOverLimit();
                    NotificationUtil.cancelDataUsageOverLimit(this.mContext);
                    this.mSimUser.setMobilePolicyEnable(false);
                    return;
                }
                NotificationUtil.sendNormalDataUsageOverWarning(this.mContext);
            }
        } else if (j2 >= this.mDataUsageTotalPackageWarning && j3 > this.mSimUser.getDataUsageOverLimitStopNetworkWarningTime()) {
            this.mSimUser.saveDataUsageOverLimitStopNetworkWarningTime(DateUtil.getThisMonthEndTimeMillis(1));
            if (this.mIsTrafficPurchaseAvailable && !this.mSimUser.isDailyUsedCardEffective()) {
                z = true;
            }
            NotificationUtil.sendNormalDataUsageWarning(this.mContext, z);
        }
    }

    private void checkNotLimitedTrafficStatus() {
        long notLimitedCardPackage = this.mSimUser.getNotLimitedCardPackage();
        long correctedMonthTotalUsed = getCorrectedMonthTotalUsed();
        if (correctedMonthTotalUsed >= notLimitedCardPackage && this.mSimUser.getNotLimitedDataUsageOverLimitStopNetworkTime() < this.mMonthStartTime) {
            this.mSimUser.saveNotLimitedDataUsageOverLimitStopNetworkTime(System.currentTimeMillis());
            NotificationUtil.sendNotLimitedDataUsageOverWarning(this.mContext, correctedMonthTotalUsed);
        }
    }

    private void checkOperatorConfig() {
        if (!this.mSimUser.isSimLocationAlertIgnore()) {
            String phoneNumber = this.mSimUser.getPhoneNumber();
            if (TextUtils.isEmpty(phoneNumber)) {
                TelephonyUtil.getPhoneNumber(this.mContext, this.mSimUser.getSlotNum(), this.mService.mHandler, new TelephonyUtil.PhoneNumberLoadedListener() {
                    public void onPhoneNumberLoaded(String str) {
                        TrafficSimManager.this.mSimUser.setPhoneNumber(str);
                        TrafficSimManager.this.checkOperatorConfig(str);
                    }
                });
            } else {
                checkOperatorConfig(phoneNumber);
            }
        }
    }

    /* access modifiers changed from: private */
    public void checkOperatorConfig(String str) {
        int[] simLocation = getSimLocation(str);
        int i = simLocation[0];
        int i2 = simLocation[1];
        if (i > -1 && i2 > -1) {
            if (i != this.mSimUser.getCity() || i2 != this.mSimUser.getProvince()) {
                this.mIsSimLocationError = true;
                NotificationUtil.sendSimLocationErrorNotify(this.mContext, this.mSimUser.getSlotNum());
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean checkPackagesConfig(TrafficUsedStatus trafficUsedStatus) {
        Object[] objArr;
        StringBuilder sb;
        String str;
        Object[] objArr2;
        StringBuilder sb2;
        String str2;
        TrafficUsedStatus trafficUsedStatus2 = trafficUsedStatus;
        if (trafficUsedStatus2 != null && trafficUsedStatus.isNormalStable()) {
            String str3 = "";
            if (Math.abs(this.mSimUser.getDataUsageTotal() - trafficUsedStatus.getTotalTrafficB()) > 1048576) {
                str3 = str3 + String.format(this.mContext.getString(R.string.traffic_config_alert_body_normal), new Object[]{FormatBytesUtil.formatBytesByMB(this.mContext, trafficUsedStatus.getTotalTrafficB()), FormatBytesUtil.formatBytesByMB(this.mContext, this.mSimUser.getDataUsageTotal())});
            }
            if (trafficUsedStatus.isLeisureEnable() && trafficUsedStatus.isLeisureStable()) {
                if (!this.mSimUser.isLeisureDataUsageOn()) {
                    sb2 = new StringBuilder();
                    sb2.append(str3);
                    str2 = this.mContext.getString(R.string.traffic_config_alert_body_leisure_notset);
                    objArr2 = new Object[]{FormatBytesUtil.formatBytesByMB(this.mContext, trafficUsedStatus.getLeisureTotalB())};
                } else if (Math.abs(this.mSimUser.getLeisureDataUsageTotal() - trafficUsedStatus.getLeisureTotalB()) > 1048576) {
                    sb2 = new StringBuilder();
                    sb2.append(str3);
                    str2 = this.mContext.getString(R.string.traffic_config_alert_body_leisure);
                    objArr2 = new Object[]{FormatBytesUtil.formatBytesByMB(this.mContext, trafficUsedStatus.getLeisureTotalB()), FormatBytesUtil.formatBytesByMB(this.mContext, this.mSimUser.getLeisureDataUsageTotal())};
                }
                sb2.append(String.format(str2, objArr2));
                str3 = sb2.toString();
            }
            if (trafficUsedStatus.isExtraEnable() && trafficUsedStatus.isExtraStable()) {
                boolean z = this.mSimUser.getDataUsageOverlayPackage() > 0;
                if (z) {
                    long dataUsageOverlayPackageTime = this.mSimUser.getDataUsageOverlayPackageTime();
                    this.mMonthStartTime = DateUtil.getThisMonthBeginTimeMillis(this.mSimUser.getMonthStart());
                    z = dataUsageOverlayPackageTime >= this.mMonthStartTime && dataUsageOverlayPackageTime <= System.currentTimeMillis();
                }
                if (!z) {
                    sb = new StringBuilder();
                    sb.append(str3);
                    str = this.mContext.getString(R.string.traffic_config_alert_body_extra_notset);
                    objArr = new Object[]{FormatBytesUtil.formatBytesByMB(this.mContext, trafficUsedStatus.getExtraTotalB())};
                } else if (Math.abs(this.mSimUser.getDataUsageOverlayPackage() - trafficUsedStatus.getExtraTotalB()) > 1048576) {
                    sb = new StringBuilder();
                    sb.append(str3);
                    str = this.mContext.getString(R.string.traffic_config_alert_body_extra);
                    objArr = new Object[]{FormatBytesUtil.formatBytesByMB(this.mContext, trafficUsedStatus.getExtraTotalB()), FormatBytesUtil.formatBytesByMB(this.mContext, this.mSimUser.getDataUsageOverlayPackage())};
                }
                sb.append(String.format(str, objArr));
                str3 = sb.toString();
            }
            String str4 = str3;
            if (!TextUtils.isEmpty(str4)) {
                if (this.mSimUser.isTrafficCorrectionAutoModify()) {
                    saveCorrectedPkgsAndUsageValues(trafficUsedStatus2, true);
                } else if (trafficUsedStatus.getTotalTrafficB() >= 0 || trafficUsedStatus.getLeisureTotalB() > 0) {
                    Context context = this.mContext;
                    if (PackageUtil.isRunningForeground(context, context.getPackageName())) {
                        startTrafficConfigAlertActivity(str4, true, trafficUsedStatus2);
                    } else {
                        NotificationUtil.sendPackageChangeNotify(this.mContext, this.mContext.getString(R.string.package_change_notification_title), this.mContext.getString(R.string.package_change_notification_summary), str4, this.mSimUser.getSlotNum(), this.mSimUser.getImsi(), trafficUsedStatus, true);
                    }
                }
                if (!this.mIsUserCorrection) {
                    this.mSimUser.setPackageChangeUpdateTime(System.currentTimeMillis());
                }
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public boolean checkTotalLimitError(TrafficUsedStatus trafficUsedStatus) {
        if (!(getCurrentMonthTotalPackage() / 1048576 < trafficUsedStatus.getRemainTrafficB() / 1048576 && trafficUsedStatus != null && trafficUsedStatus.isTotalLimitError())) {
            return false;
        }
        if (this.mSimUser.isTrafficCorrectionAutoModify()) {
            saveCorrectedPkgsAndUsageValues(trafficUsedStatus, true);
        } else {
            String format = String.format(this.mContext.getString(R.string.traffic_limit_error_alert_body), new Object[]{FormatBytesUtil.formatBytesByMB(this.mContext, trafficUsedStatus.getRemainTrafficB()), FormatBytesUtil.formatBytesByMB(this.mContext, this.mSimUser.getDataUsageTotal())});
            Context context = this.mContext;
            if (PackageUtil.isRunningForeground(context, context.getPackageName())) {
                startTrafficConfigAlertActivity(format, false, trafficUsedStatus);
            } else {
                NotificationUtil.sendPackageChangeNotify(this.mContext, this.mContext.getString(R.string.package_setted_error_notification_titile), this.mContext.getString(R.string.package_setted_error_notification_summary), format, this.mSimUser.getSlotNum(), this.mSimUser.getImsi(), trafficUsedStatus, false);
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void finishCorrection(boolean z) {
        if (this.mIsUserCorrection) {
            this.mService.mHandler.sendMessage(this.mService.mHandler.obtainMessage(z ? 17 : 18, this.mSimUser.getSlotNum(), 0));
            this.mIsUserCorrection = false;
            if (!z) {
                notifyTrafficPackageChange();
            }
        }
        if (z) {
            this.mSimUser.saveDataUsageCorrectedTime(System.currentTimeMillis());
        }
    }

    private long getCorrectedOffsetValue() {
        long dataUsageCorrectedTime = this.mSimUser.getDataUsageCorrectedTime();
        if (dataUsageCorrectedTime < this.mMonthStartTime || dataUsageCorrectedTime > System.currentTimeMillis()) {
            return 0;
        }
        return this.mSimUser.getCorrectedOffsetValue();
    }

    private long getDailyLimitTraffic() {
        int trafficLimitValue = this.mSimUser.getTrafficLimitValue();
        if (trafficLimitValue == 0) {
            return this.mSimUser.getCustomizeDailyLimitWarning();
        }
        long j = this.mDataUsageTotalPackage;
        if (j > 35651584) {
            return (j * ((long) trafficLimitValue)) / 100;
        }
        if (trafficLimitValue == 3) {
            return 1048576;
        }
        if (trafficLimitValue != 5) {
            return trafficLimitValue != 10 ? 1048576 : 3145728;
        }
        return 2097152;
    }

    private long getDataUsageForUidByFromTo(int i, long j, long j2) {
        try {
            long[] mobileHistoryForUid = this.mMiuiNetworkSessionStats.getMobileHistoryForUid(this.mCurrentImsi, i, j, j2);
            return mobileHistoryForUid[0] + mobileHistoryForUid[1];
        } catch (Exception e) {
            Log.i(TAG, "get data usage failed", e);
            return 0;
        }
    }

    public static synchronized TrafficSimManager getInstance(TrafficManageService trafficManageService, String str) {
        TrafficSimManager trafficSimManager;
        synchronized (TrafficSimManager.class) {
            trafficSimManager = sInstanceMap.get(str);
            if (trafficSimManager == null) {
                trafficSimManager = new TrafficSimManager(trafficManageService, str);
                sInstanceMap.put(str, trafficSimManager);
                trafficSimManager.initImsiRelated();
            }
        }
        return trafficSimManager;
    }

    private long getLeisureMonthDataUsage() {
        long currentTimeMillis = System.currentTimeMillis();
        long todayTimeMillis = DateUtil.getTodayTimeMillis();
        if (this.mCacheLeisureTime != todayTimeMillis) {
            long thisMonthBeginTimeMillis = DateUtil.getThisMonthBeginTimeMillis(this.mSimUser.getMonthStart());
            this.mCacheLeisureTime = todayTimeMillis;
            this.mCacheLeisureUsed = getInternalLeisureUsed(thisMonthBeginTimeMillis, todayTimeMillis);
        }
        return getInternalLeisureUsed(todayTimeMillis, currentTimeMillis) + this.mCacheLeisureUsed;
    }

    private long getMonthDataUsageUsed() {
        long j;
        if (MiSimUtil.isMiSimEnable(this.mContext, this.mSimUser.getSlotNum())) {
            this.mMiSimHelper.refreshMiSimFlowData();
            j = this.mMiSimHelper.getTotalMonthFlow() - this.mMiSimHelper.getTotalRemainedFlow();
        } else {
            j = getDataUsageByFromTo(this.mMonthStartTime, System.currentTimeMillis());
        }
        if (j > 0) {
            return j;
        }
        return 0;
    }

    /* access modifiers changed from: private */
    public long getNormalMonthDataUsageUsed() {
        long monthDataUsageUsed = (!this.mDailyCardEnable || !this.mIsDataUsageOverNormalPkg) ? getMonthDataUsageUsed() : getNormalTodayDataUsageUsed();
        if (monthDataUsageUsed > 0) {
            return monthDataUsageUsed;
        }
        return 0;
    }

    private long getNormalMonthTotalPackage() {
        long j;
        if (MiSimUtil.isMiSimEnable(this.mContext, this.mSimUser.getSlotNum())) {
            this.mMiSimHelper.refreshMiSimFlowData();
            j = this.mMiSimHelper.getTotalMonthFlow();
        } else {
            j = this.mSimUser.getDataUsageTotal();
        }
        return j + getCurrentMonthExtraPackage();
    }

    private long getRoamingTodayDataUsage() {
        long currentTimeMillis = System.currentTimeMillis();
        long roamingBeginTime = this.mSimUser.getRoamingBeginTime();
        if (currentTimeMillis - 86400000 > roamingBeginTime) {
            roamingBeginTime = DateUtil.getTodayTimeMillis();
        }
        return getDataUsageByFromTo(roamingBeginTime, currentTimeMillis);
    }

    /* access modifiers changed from: private */
    public int[] getSimLocation(String str) {
        int[] iArr = {-1, -1};
        if (!TextUtils.isEmpty(str)) {
            String b2 = b.b(this.mContext, str);
            try {
                if (!TextUtils.isEmpty(b2)) {
                    iArr[0] = Integer.parseInt(b2);
                    iArr[1] = this.mTrafficCorrection.getProvinceCodeByCityCode(iArr[0]);
                }
            } catch (NumberFormatException unused) {
                Log.i(TAG, "parse city code exception.");
            }
        }
        return iArr;
    }

    private long getTodayCorrectDataUsageUsed() {
        long normalTodayDataUsageUsed = getNormalTodayDataUsageUsed() + getCorrectedOffsetValue();
        if (normalTodayDataUsageUsed < 0) {
            return 0;
        }
        return normalTodayDataUsageUsed;
    }

    private void initDailyCardInfo() {
        this.mDailyCardEnable = this.mSimUser.isDailyUsedCardEffective();
        if (this.mDailyCardEnable) {
            if (!this.mSimUser.isTotalDataUsageSetted()) {
                this.mSimUser.saveDataUsageTotal(0);
            }
            this.mDailyCardPackage = this.mSimUser.getDailyUsedCardPackage();
            if (!DateUtil.isTheSameDay(this.mSimUser.getDailyUsedCardDataUpdateTime(), System.currentTimeMillis())) {
                this.mSimUser.setDailyUsedCardDataUpdateTime(0);
                this.mSimUser.saveCorrectedOffsetValue(0);
                this.mSimUser.setDailyUsedCardStopNetworkCount((int) (getTodayCorrectDataUsageUsed() / this.mDailyCardPackage));
            }
        }
    }

    private void initImsiRelated() {
        initSimInfo();
        initTrafficCorrection();
        initMobileStatistic();
        initTrafficStatusMonitorVariable();
    }

    private void initMobileStatistic() {
        if (!TextUtils.equals(this.mCurrentImsi, "default")) {
            this.mMiuiNetworkSessionStats = new MiuiNetworkSessionStats(this.mContext);
            this.mMiuiNetworkSessionStats.openSession();
        }
    }

    private void initSimInfo() {
        this.mSimUser = SimUserInfo.getInstance(this.mContext, this.mCurrentImsi);
        if (this.mSimUser.isSimInserted() && this.mSimUser.hasImsi()) {
            checkNormalTotalPackageSetted();
        }
    }

    private void initTrafficCorrection() {
        this.mTrafficCorrection = TrafficCorrectionManager.getTrafficCorrectionInstance(this.mContext, this.mSimUser.getImsi(), this.mSimUser.getSlotNum());
        this.mTrafficCorrection.registerLisener(this.mTrafficCorrectionListener);
    }

    /* access modifiers changed from: private */
    public void notifyBillPackageChange() {
        this.mContext.getContentResolver().notifyChange(Uri.parse("content://" + String.format("%s/%s", new Object[]{ProviderConstant.AUTHORITY, "bill_detail"})), (ContentObserver) null);
    }

    private void notifyCallTimePackageChange() {
        this.mContext.getContentResolver().notifyChange(Uri.parse("content://" + String.format("%s/%s", new Object[]{ProviderConstant.AUTHORITY, "calltime_detail"})), (ContentObserver) null);
    }

    /* access modifiers changed from: private */
    public void notifyTrafficPackageChange() {
        this.mContext.getContentResolver().notifyChange(Uri.parse("content://" + String.format("%s/%s", new Object[]{ProviderConstant.AUTHORITY, "datausage_status"})), (ContentObserver) null);
    }

    private void onNormalTrafficOverLimit() {
        int i = 0;
        TelephonyUtil.setMobileDataState(this.mContext, false);
        Settings.Secure.putInt(this.mContext.getContentResolver(), Constants.System.MOBILE_POLICY, 0);
        if (TelephonyUtil.isPhoneIdleState(this.mContext)) {
            Intent intent = new Intent();
            intent.setClass(this.mContext, NetworkOverLimitActivity.class);
            intent.addFlags(268435456);
            g.b(this.mContext, intent, B.b());
            return;
        }
        int overDataUsageStopNetworkType = this.mSimUser.getOverDataUsageStopNetworkType();
        if (this.mDailyCardEnable) {
            i = this.mSimUser.getDailyUsedCardStopNetworkCount() + 1;
        }
        NotificationUtil.sendDataUsageOverLimit(this.mContext, overDataUsageStopNetworkType, i);
    }

    /* access modifiers changed from: private */
    public void saveAnalytics(TrafficUsedStatus trafficUsedStatus) {
        AnalyticsHelper.trackTrafficCorrectionResult(trafficUsedStatus);
        AnalyticsHelper.trackTrafficSmsCorrection(String.valueOf(this.mSimUser.getBrand()), String.valueOf(this.mSimUser.getProvince()), trafficUsedStatus.getReturnCode(), trafficUsedStatus.getCorrectionType());
    }

    /* access modifiers changed from: private */
    public void saveBillAndCallTimeResult(TrafficUsedStatus trafficUsedStatus) {
        if (trafficUsedStatus.isBillEnabled()) {
            this.mSimUser.setBillPackageTotal(trafficUsedStatus.getBillTotal());
            this.mSimUser.setBillPackageRemained(trafficUsedStatus.getBillRemained());
        }
        if (trafficUsedStatus.isCallTimeEnabled()) {
            this.mSimUser.setCallTimePackageTotal(trafficUsedStatus.getCallTimeTotal());
            this.mSimUser.setCallTimePackageRemained(trafficUsedStatus.getCallTimeRemained());
        }
    }

    private void saveCorrectedPkgs(TrafficUsedStatus trafficUsedStatus) {
        if (trafficUsedStatus.isTotalLimitError()) {
            long remainTrafficB = (trafficUsedStatus.getRemainTrafficB() / 1048576) * 1048576;
            if (this.mSimUser.getPackageChangeUpdateTime() < this.mMonthStartTime) {
                this.mSimUser.setPackageChangeUpdateTime(System.currentTimeMillis());
                this.mSimUser.saveDataUsageTotal(remainTrafficB);
            } else {
                this.mSimUser.saveDataUsageOverlayPackage(remainTrafficB - this.mSimUser.getDataUsageTotal());
                this.mSimUser.saveDataUsageOverlayPackageTime(System.currentTimeMillis());
            }
        }
        if (trafficUsedStatus.isNormalStable()) {
            this.mSimUser.saveDataUsageTotal((trafficUsedStatus.getTotalTrafficB() / 1048576) * 1048576);
        }
        if (trafficUsedStatus.isLeisureEnable() && trafficUsedStatus.isLeisureStable()) {
            this.mSimUser.toggleLeisureDataUsageOn(true);
            this.mSimUser.saveLeisureDataUsageTotal((trafficUsedStatus.getLeisureTotalB() / 1048576) * 1048576);
        }
        if (trafficUsedStatus.isExtraEnable() && trafficUsedStatus.isExtraStable()) {
            this.mSimUser.saveDataUsageOverlayPackage((trafficUsedStatus.getExtraTotalB() / 1048576) * 1048576);
            this.mSimUser.saveDataUsageOverlayPackageTime(System.currentTimeMillis());
        }
        updateTrafficCorrectionTotalLimit();
        showAutoModifyPackageAlert();
    }

    private void saveCorrectedUsageValues(TrafficUsedStatus trafficUsedStatus) {
        long j;
        long j2;
        long usedTrafficB = trafficUsedStatus.getUsedTrafficB();
        if (trafficUsedStatus.isExtraEnable()) {
            usedTrafficB += trafficUsedStatus.getExtraUsedB();
        }
        boolean z = false;
        if (this.mSimUser.isLeisureDataUsageEffective()) {
            long[] correctedNormalAndLeisureMonthTotalUsed = getCorrectedNormalAndLeisureMonthTotalUsed();
            j2 = correctedNormalAndLeisureMonthTotalUsed[0];
            j = correctedNormalAndLeisureMonthTotalUsed[1];
        } else {
            j2 = getCorrectedNormalMonthTotalUsed();
            j = 0;
        }
        boolean z2 = !trafficUsedStatus.isNormalJustOver() || j2 <= usedTrafficB;
        long leisureUsedB = trafficUsedStatus.getLeisureUsedB();
        if (!trafficUsedStatus.isLeisureJustOver() || j <= leisureUsedB) {
            z = true;
        }
        if (z2) {
            saveLatestCorrectedNormalDataUsage(usedTrafficB);
        }
        if (trafficUsedStatus.isLeisureEnable() && z) {
            saveLatestCorrectedLeisureDataUsage(leisureUsedB);
        }
    }

    /* access modifiers changed from: private */
    public void shouldUpdateTcEngine(TrafficUsedStatus trafficUsedStatus) {
        if (this.mShouldUpdateTcEngine && trafficUsedStatus.getCorrectionType() == 2) {
            this.mShouldUpdateTcEngine = false;
            a.a(new Runnable() {
                public void run() {
                    if (!TrafficSimManager.this.mIsUserCorrection) {
                        boolean unused = TrafficSimManager.this.updateTrafficCorrectionEngine();
                    }
                }
            });
        }
    }

    private void showAutoModifyPackageAlert() {
        if (!this.mIsUserCorrection && this.mSimUser.isTrafficCorrectionAutoModify()) {
            Context context = this.mContext;
            if (PackageUtil.isRunningForeground(context, context.getPackageName())) {
                NotificationUtil.sendCorrectionAlertNotify(this.mContext, this.mContext.getString(R.string.traffic_alert_auto_modify_notification_titile), String.format(this.mContext.getString(R.string.traffic_alert_auto_modify_notification_sumarry), new Object[]{FormatBytesUtil.formatBytesByMB(this.mContext, getCurrentMonthTotalPackage())}), this.mSimUser.getSlotNum());
                return;
            }
        }
        this.mService.mHandler.sendMessage(this.mService.mHandler.obtainMessage(20, this.mSimUser.getSlotNum(), R.string.traffic_config_alert_setted));
    }

    /* access modifiers changed from: private */
    public void showCorrectionFailedToast() {
        this.mService.mHandler.sendMessage(this.mService.mHandler.obtainMessage(18, this.mSimUser.getSlotNum(), 0));
    }

    private void showCorrectionStartedToast() {
        this.mService.mHandler.sendMessage(this.mService.mHandler.obtainMessage(16, this.mSimUser.getSlotNum(), R.string.traffic_correction_start_correction));
    }

    /* access modifiers changed from: private */
    public void showDataUsageCorrectionFailureNotify(Context context, int i) {
        if (this.mIsUserCorrection) {
            NotificationUtil.sendDataUsageCorrectionTimeOutOrFailureNotify(context, context.getResources().getString(R.string.tc_sms_report_notify_correction_failure_title), context.getResources().getString(R.string.tc_sms_report_notify_correction_body), i);
        }
    }

    /* access modifiers changed from: private */
    public void showDataUsageCorrectionTimeOutNotify(Context context, int i) {
        if (this.mIsUserCorrection) {
            NotificationUtil.sendDataUsageCorrectionTimeOutOrFailureNotify(context, context.getResources().getString(R.string.data_usage_correction_timeout_title), context.getResources().getString(R.string.tc_sms_report_notify_correction_body), i);
        }
    }

    private void showTrafficInStatusBar() {
        if (!this.mCommonConfig.isStatusBarShowTrafficUpdate()) {
            int i = Settings.System.getInt(this.mContext.getContentResolver(), Constants.System.STATUS_BAR_SHOW_NETWORK_ASSISTANT, 0);
            this.mCommonConfig.setStatusBarShowTrafficUpdate(true);
            if (i == 0) {
                Settings.System.putInt(this.mContext.getContentResolver(), Constants.System.STATUS_BAR_SHOW_NETWORK_ASSISTANT, 1);
            }
        }
    }

    private void startTrafficConfigAlertActivity(String str, boolean z, TrafficUsedStatus trafficUsedStatus) {
        Intent intent = new Intent(this.mContext, TrafficConfigAlertActivity.class);
        intent.putExtra(TrafficConfigAlertActivity.BUNDLE_KEY_IS_STABLE_PKG, z);
        intent.putExtra(TrafficConfigAlertActivity.BUNDLE_KEY_BODY, str);
        intent.putExtra(Sim.SIM_SLOT_NUM_TAG, this.mSimUser.getSlotNum());
        intent.putExtra(TrafficConfigAlertActivity.BUNDLE_KEY_IMSI, this.mSimUser.getImsi());
        intent.putExtra(TrafficConfigAlertActivity.BUNDLE_KEY_TRAFFIC_USED_STATUS, trafficUsedStatus);
        intent.setFlags(268435456);
        g.b(this.mContext, intent, B.b());
    }

    /* access modifiers changed from: private */
    public void startTrafficCorrection(boolean z, long j, int i) {
        HashMap hashMap;
        if (!this.mSimUser.isCustomizedSms() || this.mSimUser.isDailyUsedCardEffective()) {
            hashMap = null;
        } else {
            hashMap = new HashMap();
            String customizedSmsNum = this.mSimUser.getCustomizedSmsNum();
            String customizedSmsContent = this.mSimUser.getCustomizedSmsContent();
            if (!TextUtils.isEmpty(customizedSmsNum) && !TextUtils.isEmpty(customizedSmsContent)) {
                hashMap.put(customizedSmsNum, customizedSmsContent);
            }
        }
        int checkCorrectionType = checkCorrectionType(i);
        boolean startCorrection = this.mTrafficCorrection.startCorrection(z, hashMap, j, checkCorrectionType);
        this.mIsUserCorrection = !z;
        if (this.mIsUserCorrection && !startCorrection) {
            this.mService.mHandler.sendMessage(this.mService.mHandler.obtainMessage(16, this.mSimUser.getSlotNum(), R.string.traffic_correction_start_correction_failed));
        }
    }

    /* access modifiers changed from: private */
    public boolean updateTrafficCorrectionEngine() {
        SimUserInfo simUserInfo = this.mSimUser;
        if (simUserInfo != null && simUserInfo.isOperatorSetted()) {
            try {
                checkOperatorConfig();
                boolean updateSMSTemplate = this.mTrafficCorrection.updateSMSTemplate(String.valueOf(this.mSimUser.getProvince()), String.valueOf(this.mSimUser.getCity()), this.mSimUser.getOperator());
                if (updateSMSTemplate) {
                    this.mSimUser.setTrafficCorrectionEngineUpdateTime(System.currentTimeMillis());
                }
                Log.i(TAG, String.format("mina update correction engine, result:%b, slotNum:%d", new Object[]{Boolean.valueOf(updateSMSTemplate), Integer.valueOf(this.mSimUser.getSlotNum())}));
                return updateSMSTemplate;
            } catch (Error e) {
                Log.i(TAG, "update engine exception", e);
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void BillAndCallTimePackage() {
        this.mSimUser.setBillPackageTotal(-1);
        this.mSimUser.setBillPackageRemained(-1);
        this.mSimUser.setCallTimePackageTotal(-1);
        this.mSimUser.setCallTimePackageRemained(-1);
    }

    /* access modifiers changed from: package-private */
    public void checkActiveSlotTraffic() {
        this.mMonthStartTime = DateUtil.getThisMonthBeginTimeMillis(this.mSimUser.getMonthStart());
        if (this.mIsDataUsageOverLimitOn && this.mSimUser.getDataUsageOverLimitStopNetworkTime() > this.mMonthStartTime && !this.mSimUser.isMobilePolicyEnable() && this.mService.getMobileDataPolicy() == 1) {
            miui.util.Log.getFullLogger().info(TAG, "checkActiveSlotTraffic");
            onNormalTrafficOverLimit();
        }
    }

    /* access modifiers changed from: package-private */
    public void checkDailyLimit() {
        Log.i(TAG, String.format("daily limit traffic %s", new Object[]{Long.valueOf(getNormalTodayDataUsageNoLeisureUsed())}));
        if (this.mDataUsageTotalPackage - getCorrectedNormalMonthTotalUsed() > 0 && DateUtil.isLastDayOfMonth()) {
            Log.i(TAG, "checkDailyLimit -- isLastDayOfMonth: true !");
        } else if (getNormalTodayDataUsageNoLeisureUsed() >= this.mDailyLimitTraffic) {
            int i = this.mOverDailyLimitWarningType;
            if (i != 0) {
                if (i == 1) {
                    Log.i(TAG, String.format("policy %d", new Object[]{Integer.valueOf(this.mService.getMobileDataPolicy())}));
                    if (this.mService.getMobileDataPolicy() == 1 && this.mSimUser.getDataUsageOverDailyLimitTime() < DateUtil.getTodayTimeMillis()) {
                        this.mSimUser.setDataUsageOverDailyLimitTime(System.currentTimeMillis());
                        this.mSimUser.setOverDataUsageStopNetworkType(1);
                        onNormalTrafficOverLimit();
                    }
                }
            } else if (this.mSimUser.getDataUsageOverDailyLimitTime() < DateUtil.getTodayTimeMillis()) {
                this.mSimUser.setDataUsageOverDailyLimitTime(System.currentTimeMillis());
                NotificationUtil.sendDailyLimitWarning(this.mContext);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void checkMiMobileOperatorConfig() {
        SimUserInfo simUserInfo;
        if (PrivacyDeclareAndAllowNetworkUtil.isAllowNetwork() && f.j(this.mContext) && (simUserInfo = this.mSimUser) != null && simUserInfo.isSimInserted() && this.mSimUser.hasImsi() && !this.mSimUser.isMiMobileOperatorModify() && !this.mSimUser.isOversea() && !this.mSimUser.isDataRoaming() && !this.mSimUser.isTotalDataUsageSetted()) {
            a.a(new Runnable() {
                public void run() {
                    long access$300 = TrafficSimManager.this.getNormalMonthDataUsageUsed();
                    String phoneNumber = TrafficSimManager.this.mSimUser.getPhoneNumber();
                    if (TextUtils.isEmpty(phoneNumber)) {
                        phoneNumber = TelephonyUtil.getPhoneNumber(TrafficSimManager.this.mContext, TrafficSimManager.this.mSimUser.getSlotNum());
                        TrafficSimManager.this.mSimUser.setPhoneNumber(phoneNumber);
                    }
                    if (!TextUtils.isEmpty(phoneNumber) || TrafficSimManager.this.mSimUser.hasImsi()) {
                        DataUsageResult queryDataUsage = WebApiAccessHelper.queryDataUsage(TrafficSimManager.this.mSimUser.getImsi(), String.valueOf(TrafficSimManager.this.mSimUser.getCity()), phoneNumber, TrafficSimManager.this.mSimUser.getOperator(), access$300, TrafficSimManager.this.mSimUser.getIccid());
                        TrafficSimManager.this.mSimUser.setMiMobileOperatorModify(true);
                        if (queryDataUsage != null && queryDataUsage.isSuccess()) {
                            Log.i(TrafficSimManager.TAG, "modify mi mobile operator setting " + queryDataUsage.toString());
                            TrafficSimManager.this.mSimUser.saveOperator(queryDataUsage.getOperator());
                            if (queryDataUsage.getProvinceCode() <= -1 || queryDataUsage.getCityCode() <= -1) {
                                int[] access$600 = TrafficSimManager.this.getSimLocation(phoneNumber);
                                if (access$600[0] > -1 && access$600[1] > -1) {
                                    TrafficSimManager.this.mSimUser.saveCity(access$600[0]);
                                    TrafficSimManager.this.mSimUser.saveProvince(access$600[1]);
                                }
                            } else {
                                TrafficSimManager.this.mSimUser.saveCity(queryDataUsage.getCityCode());
                                TrafficSimManager.this.mSimUser.saveProvince(queryDataUsage.getProvinceCode());
                            }
                            TrafficSimManager.this.mSimUser.saveDataUsageTotal(queryDataUsage.getTotal());
                            TrafficSimManager.this.saveLatestCorrectedNormalDataUsage(queryDataUsage.getUsedFlow());
                            if (queryDataUsage.getTotal() <= 0) {
                                TrafficSimManager.this.mSimUser.toggleDataUsageOverLimitStopNetwork(false);
                            }
                            TrafficSimManager.this.mSimUser.toggleDataUsageAutoCorrection(true);
                            TrafficSimManager.this.mService.initDataUsageAutoCorrection();
                            if (queryDataUsage.isBillOn()) {
                                TrafficSimManager.this.mSimUser.setBillPackageRemained(queryDataUsage.getBillLeft());
                            }
                            if (queryDataUsage.isCallTimeOn()) {
                                TrafficSimManager.this.mSimUser.setCallTimePackageTotal(queryDataUsage.getCallTimeTotal());
                                TrafficSimManager.this.mSimUser.setCallTimePackageRemained(queryDataUsage.getCallTimeLeft());
                            }
                            NotificationUtil.cancelNormalTotalPackageNotSetted(TrafficSimManager.this.mContext);
                        } else if (queryDataUsage != null) {
                            Log.i(TrafficSimManager.TAG, "failed result : " + queryDataUsage.toString());
                        }
                    }
                }
            });
        }
    }

    /* access modifiers changed from: package-private */
    public void checkNormalTotalPackageSetted() {
        if (LoadConfigUtil.isDataUsageLimitAlertEnabled(this.mContext)) {
            if ((!DeviceUtil.IS_DUAL_CARD || Sim.getCurrentActiveSlotNum() == this.mSimUser.getSlotNum()) && !MiSimUtil.isMiSimEnable(this.mContext, this.mSimUser.getSlotNum()) && this.mSimUser.isSimInserted() && !this.mSimUser.isTotalDataUsageSetted() && this.mService.isDeviceProvisioned() && !this.mSimUser.isOversea() && !this.mSimUser.isDataUsageTotalNotSetNotified()) {
                NotificationUtil.sendNormalTotalPackageNotSetted(this.mContext, this.mSimUser.getSlotNum());
                this.mSimUser.setDataUsageTotalNotSetNotified(true);
                this.mSimUser.setNATipsEnable(true);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void checkRoamingDailyLimit() {
        long roamingTodayDataUsage = getRoamingTodayDataUsage();
        long j = this.mRoamingDailyLimitTraffic;
        if (j > 0 && roamingTodayDataUsage > j) {
            int i = this.mRoamingOverLimitOptType;
            if (i != 0) {
                if (i == 1 && this.mService.getMobileDataPolicy() == 1 && this.mSimUser.getDataUsageOverRoamingDailyLimitTime() < DateUtil.getTodayTimeMillis()) {
                    this.mSimUser.setDataUsageOverRoamingDailyLimitTime(System.currentTimeMillis());
                    this.mSimUser.setOverDataUsageStopNetworkType(2);
                    onNormalTrafficOverLimit();
                }
            } else if (this.mSimUser.getDataUsageOverRoamingDailyLimitTime() < DateUtil.getTodayTimeMillis()) {
                this.mSimUser.setDataUsageOverRoamingDailyLimitTime(System.currentTimeMillis());
                NotificationUtil.sendRoamingDailyLimitWarning(this.mContext);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void checkTrafficCorrectionEngineUpdate() {
        if (this.mSimUser.isOperatorSetted() && this.mSimUser.getTrafficCorrectionEngineUpdateTime() + 604800000 < System.currentTimeMillis()) {
            updateTrafficCorrectionEngine();
        }
    }

    /* access modifiers changed from: package-private */
    public void checkTrafficSettingAndSendNotification() {
        if (this.mSimUser.isTrafficManageControlEnable() && this.mSimUser != null && !this.mIsMiSim) {
            long correctedNormalMonthTotalUsed = getCorrectedNormalMonthTotalUsed();
            long normalTodayDataUsageUsed = getNormalTodayDataUsageUsed();
            boolean isTotalDataUsageSetted = this.mSimUser.isTotalDataUsageSetted();
            boolean isOperatorSetted = this.mSimUser.isOperatorSetted();
            long currentTimeMillis = System.currentTimeMillis();
            if ((!isTotalDataUsageSetted || !isOperatorSetted) && !this.mSimUser.isOversea()) {
                boolean z = currentTimeMillis >= DateUtil.getTodayTimeMillis() + 72000000;
                long thisMonthBeginTimeMillis = DateUtil.getThisMonthBeginTimeMillis(0);
                if (DateUtil.getDayOfMonth() == 18 && z && correctedNormalMonthTotalUsed >= 1048576 && this.mSimUser.getTrafficSettingMonthlyNotifyUpdateTime() < thisMonthBeginTimeMillis) {
                    NotificationUtil.cancelNormalTotalPackageNotSetted(this.mContext);
                    NotificationUtil.sendTrafficSettingMonthlyNotify(this.mContext, correctedNormalMonthTotalUsed);
                    this.mSimUser.setNATipsEnable(true);
                    this.mSimUser.setTrafficSettingMonthlyNotifyUpdateTime(System.currentTimeMillis());
                }
                if (normalTodayDataUsageUsed >= 20971520 && this.mSimUser.getTrafficSettingDailyNotifyUpdateTime()) {
                    NotificationUtil.cancelNormalTotalPackageNotSetted(this.mContext);
                    NotificationUtil.sendTrafficSettingDailyNotify(this.mContext, normalTodayDataUsageUsed);
                    this.mSimUser.setNATipsEnable(true);
                    this.mSimUser.setTrafficSettingDailyNotifyUpdateTime(false);
                }
            }
            boolean dailyLimitEnabled = this.mSimUser.getDailyLimitEnabled();
            long currentMonthTotalPackage = getCurrentMonthTotalPackage();
            long trafficSettingDailyLimitNotifyUpdateTime = this.mSimUser.getTrafficSettingDailyLimitNotifyUpdateTime();
            double d2 = ((double) currentMonthTotalPackage) * 0.05d;
            double d3 = 3.145728E7d;
            if (d2 > 3.145728E7d) {
                d3 = d2;
            }
            if (isTotalDataUsageSetted && !dailyLimitEnabled && currentMonthTotalPackage < PermissionManager.PERM_ID_READCONTACT && ((double) normalTodayDataUsageUsed) > d3 && currentTimeMillis > trafficSettingDailyLimitNotifyUpdateTime) {
                NotificationUtil.sendSettingDailyLimitNotify(this.mContext, normalTodayDataUsageUsed);
                this.mSimUser.setTrafficSettingDailyLimitNotifyUpdateTime(DateUtil.getThisMonthEndTimeMillis(1) + 86400000);
            }
            this.mMonthStartTime = DateUtil.getThisMonthBeginTimeMillis(this.mSimUser.getMonthStart());
            if (DeviceUtil.IS_INTERNATIONAL_BUILD) {
                return;
            }
            if ((!isTotalDataUsageSetted || this.mSimUser.getDataUsageOverLimitStopNetworkTime() > this.mMonthStartTime) && !this.mSimUser.isDailyUsedCardEnable() && !this.mSimUser.isNotLimitCardEnable() && correctedNormalMonthTotalUsed >= 5368709120L && this.mSimUser.getTrafficProtectedStopNetTime() < this.mMonthStartTime) {
                this.mSimUser.setTrafficProtectedStopNetTime(DateUtil.getThisMonthEndTimeMillis(1) + 86400000);
                this.mSimUser.setOverDataUsageStopNetworkType(5);
                onNormalTrafficOverLimit();
                this.mSimUser.setMobilePolicyEnable(false);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void checkTrafficStatus() {
        if (this.mSimUser.isTrafficManageControlEnable() && !this.mIsMiSim) {
            if (this.mSimUser.isNotLimitCardEnable()) {
                checkNotLimitedTrafficStatus();
            } else if (this.mIsTotalPackageSetted) {
                if (this.mDailyCardEnable) {
                    checkDailyUsedTrafficStatus();
                } else if (this.mSimUser.isLeisureDataUsageEffective()) {
                    checkNormalAndLeisureTrafficStatus();
                } else {
                    checkNormalTrafficStatus();
                }
            }
            if (this.mIsRoamingDailyLimitEnabled && this.mSimUser.isDataRoaming()) {
                checkRoamingDailyLimit();
            } else if (this.mIsDailyLimitEnabled && !this.mSimUser.isNotLimitCardEnable()) {
                checkDailyLimit();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void clearAllLimitTime() {
        this.mSimUser.saveDataUsageOverLimitStopNetworkWarningTime(0);
        this.mSimUser.saveDataUsageOverLimitStopNetworkTime(0);
        this.mSimUser.saveLeisureDataUsageOverLimitWarningTime(0);
        this.mSimUser.saveLeisureOverLimitStopNetworkTime(0);
        this.mSimUser.setDataUsageOverDailyLimitTime(0);
    }

    /* access modifiers changed from: package-private */
    public void clearDailyLimitTime() {
        this.mSimUser.setDataUsageOverDailyLimitTime(0);
    }

    /* access modifiers changed from: package-private */
    public void clearRoamingDailyLimitTime() {
        this.mSimUser.setDataUsageOverRoamingDailyLimitTime(0);
    }

    /* access modifiers changed from: package-private */
    public void forceUpdateTraffic() {
        a.a(new Runnable() {
            public void run() {
                if (TrafficSimManager.this.mMiuiNetworkSessionStats != null) {
                    TrafficSimManager.this.mMiuiNetworkSessionStats.forceUpdate();
                }
            }
        });
    }

    /* access modifiers changed from: package-private */
    public long getCorrectedMonthTotalUsed() {
        long monthDataUsageUsed = getMonthDataUsageUsed() + getCorrectedOffsetValue();
        if (monthDataUsageUsed < 0) {
            return 0;
        }
        return monthDataUsageUsed;
    }

    /* access modifiers changed from: package-private */
    public long[] getCorrectedNormalAndLeisureMonthTotalUsed() {
        long[] correctedNormalAndLeisureMonthTotalUsedNoAligned = getCorrectedNormalAndLeisureMonthTotalUsedNoAligned();
        correctedNormalAndLeisureMonthTotalUsedNoAligned[0] = correctedNormalAndLeisureMonthTotalUsedNoAligned[0] + getCorrectedOffsetValue();
        long leisureDataUsageCorrectedTime = this.mSimUser.getLeisureDataUsageCorrectedTime();
        if (leisureDataUsageCorrectedTime >= this.mMonthStartTime && leisureDataUsageCorrectedTime <= System.currentTimeMillis()) {
            correctedNormalAndLeisureMonthTotalUsedNoAligned[1] = correctedNormalAndLeisureMonthTotalUsedNoAligned[1] + this.mSimUser.getLeisureDataUsageCorrectedValue();
        }
        long leisureDataUsageTotal = this.mSimUser.getLeisureDataUsageTotal();
        if (correctedNormalAndLeisureMonthTotalUsedNoAligned[1] > leisureDataUsageTotal) {
            correctedNormalAndLeisureMonthTotalUsedNoAligned[0] = correctedNormalAndLeisureMonthTotalUsedNoAligned[0] + (correctedNormalAndLeisureMonthTotalUsedNoAligned[1] - leisureDataUsageTotal);
            correctedNormalAndLeisureMonthTotalUsedNoAligned[1] = leisureDataUsageTotal;
        }
        if (correctedNormalAndLeisureMonthTotalUsedNoAligned[0] < 0) {
            correctedNormalAndLeisureMonthTotalUsedNoAligned[0] = 0;
        }
        if (correctedNormalAndLeisureMonthTotalUsedNoAligned[1] < 0) {
            correctedNormalAndLeisureMonthTotalUsedNoAligned[1] = 0;
        }
        return correctedNormalAndLeisureMonthTotalUsedNoAligned;
    }

    /* access modifiers changed from: package-private */
    public long[] getCorrectedNormalAndLeisureMonthTotalUsedNoAligned() {
        long[] jArr = new long[2];
        long normalMonthDataUsageUsed = getNormalMonthDataUsageUsed();
        long leisureMonthDataUsage = getLeisureMonthDataUsage();
        long leisureDataUsageTotal = this.mSimUser.getLeisureDataUsageTotal();
        if (leisureMonthDataUsage > leisureDataUsageTotal) {
            jArr[0] = normalMonthDataUsageUsed - leisureDataUsageTotal;
            jArr[1] = leisureDataUsageTotal;
        } else {
            jArr[0] = normalMonthDataUsageUsed - leisureMonthDataUsage;
            jArr[1] = leisureMonthDataUsage;
        }
        return jArr;
    }

    /* access modifiers changed from: package-private */
    public long getCorrectedNormalMonthTotalUsed() {
        long normalMonthDataUsageUsed = getNormalMonthDataUsageUsed() + getCorrectedOffsetValue();
        if (normalMonthDataUsageUsed < 0) {
            return 0;
        }
        return normalMonthDataUsageUsed;
    }

    /* access modifiers changed from: package-private */
    public long getCurrentMonthExtraPackage() {
        if (DateUtil.isCurrentCycleMonth(this.mSimUser.getDataUsageOverlayPackageTime(), this.mSimUser.getMonthStart())) {
            return this.mSimUser.getDataUsageOverlayPackage();
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public long getCurrentMonthTotalPackage() {
        return (!this.mDailyCardEnable || !this.mIsDataUsageOverNormalPkg) ? getNormalMonthTotalPackage() : ((long) (this.mSimUser.getDailyUsedCardStopNetworkCount() + 1)) * this.mSimUser.getDailyUsedCardPackage();
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0039, code lost:
        if (r11 >= 0) goto L_0x003c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:?, code lost:
        return r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:?, code lost:
        return 0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long getDataUsageByFromTo(long r14, long r16) {
        /*
            r13 = this;
            r7 = r13
            r8 = 0
            com.miui.net.MiuiNetworkSessionStats r0 = r7.mMiuiNetworkSessionStats     // Catch:{ NullPointerException -> 0x004a }
            if (r0 == 0) goto L_0x0052
            com.miui.net.MiuiNetworkSessionStats r1 = r7.mMiuiNetworkSessionStats     // Catch:{ NullPointerException -> 0x004a }
            java.lang.String r2 = r7.mCurrentImsi     // Catch:{ NullPointerException -> 0x004a }
            r3 = r14
            r5 = r16
            long r1 = r1.getNetworkMobileTotalBytes(r2, r3, r5)     // Catch:{ NullPointerException -> 0x004a }
            java.util.HashSet<java.lang.Integer> r10 = r7.mDataUsageIgnoreUidListSelfLocked     // Catch:{ NullPointerException -> 0x0047 }
            monitor-enter(r10)     // Catch:{ NullPointerException -> 0x0047 }
            java.util.HashSet<java.lang.Integer> r0 = r7.mDataUsageIgnoreUidListSelfLocked     // Catch:{ all -> 0x0041 }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x0041 }
            r11 = r1
        L_0x001c:
            boolean r1 = r0.hasNext()     // Catch:{ all -> 0x003e }
            if (r1 == 0) goto L_0x0036
            java.lang.Object r1 = r0.next()     // Catch:{ all -> 0x003e }
            java.lang.Integer r1 = (java.lang.Integer) r1     // Catch:{ all -> 0x003e }
            int r2 = r1.intValue()     // Catch:{ all -> 0x003e }
            r1 = r13
            r3 = r14
            r5 = r16
            long r1 = r1.getDataUsageForUidByFromTo(r2, r3, r5)     // Catch:{ all -> 0x003e }
            long r11 = r11 - r1
            goto L_0x001c
        L_0x0036:
            monitor-exit(r10)     // Catch:{ all -> 0x003e }
            int r0 = (r11 > r8 ? 1 : (r11 == r8 ? 0 : -1))
            if (r0 >= 0) goto L_0x003c
            goto L_0x0052
        L_0x003c:
            r8 = r11
            goto L_0x0052
        L_0x003e:
            r0 = move-exception
            r8 = r11
            goto L_0x0043
        L_0x0041:
            r0 = move-exception
            r8 = r1
        L_0x0043:
            monitor-exit(r10)     // Catch:{ all -> 0x0045 }
            throw r0     // Catch:{ NullPointerException -> 0x004a }
        L_0x0045:
            r0 = move-exception
            goto L_0x0043
        L_0x0047:
            r0 = move-exception
            r8 = r1
            goto L_0x004b
        L_0x004a:
            r0 = move-exception
        L_0x004b:
            java.lang.String r1 = "TrafficManageService"
            java.lang.String r2 = "get data usage failed."
            android.util.Log.i(r1, r2, r0)
        L_0x0052:
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.service.tm.TrafficSimManager.getDataUsageByFromTo(long, long):long");
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:6:0x001b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long getInternalLeisureUsed(long r11, long r13) {
        /*
            r10 = this;
            long r0 = r10.mLeisureFromTime
            long r2 = r11 + r0
            long r4 = r10.mLeisureToTime
            long r6 = r11 + r4
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            r4 = 86400000(0x5265c00, double:4.2687272E-316)
            r8 = 0
            if (r0 <= 0) goto L_0x0017
            long r11 = r10.getDataUsageByFromTo(r11, r6)
            long r8 = r8 + r11
        L_0x0016:
            long r6 = r6 + r4
        L_0x0017:
            int r11 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1))
            if (r11 >= 0) goto L_0x0022
            long r11 = r10.getDataUsageByFromTo(r2, r6)
            long r8 = r8 + r11
            long r2 = r2 + r4
            goto L_0x0016
        L_0x0022:
            int r11 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r11 >= 0) goto L_0x002b
            long r11 = r10.getDataUsageByFromTo(r2, r13)
            long r8 = r8 + r11
        L_0x002b:
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.service.tm.TrafficSimManager.getInternalLeisureUsed(long, long):long");
    }

    /* access modifiers changed from: package-private */
    public long getNormalTodayDataUsageNoLeisureUsed() {
        long todayTimeMillis = DateUtil.getTodayTimeMillis();
        long j = 86400000 + todayTimeMillis;
        return this.mSimUser.isLeisureDataUsageEffective() ? getDataUsageByFromTo(todayTimeMillis, j) - getInternalLeisureUsed(todayTimeMillis, j) : getNormalTodayDataUsageUsed();
    }

    /* access modifiers changed from: package-private */
    public long getNormalTodayDataUsageUsed() {
        return getDataUsageByFromTo(DateUtil.getTodayTimeMillis(), System.currentTimeMillis());
    }

    /* access modifiers changed from: package-private */
    public long getTodayLeisureDataUsage() {
        long todayTimeMillis = DateUtil.getTodayTimeMillis();
        long j = 86400000 + todayTimeMillis;
        if (this.mSimUser.isLeisureDataUsageEffective()) {
            return getInternalLeisureUsed(todayTimeMillis, j);
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public void initDataUsageIgnoreAppList() {
        synchronized (this.mDataUsageIgnoreUidListSelfLocked) {
            if (this.mSimUser.hasImsi()) {
                this.mIgnoreAppListConfig = DataUsageIgnoreAppListConfig.getInstance(this.mContext, this.mCurrentImsi);
            }
            if (this.mIgnoreAppListConfig != null) {
                ArrayList<String> ignoreList = this.mIgnoreAppListConfig.getIgnoreList();
                this.mDataUsageIgnoreUidListSelfLocked.clear();
                Iterator<String> it = ignoreList.iterator();
                while (it.hasNext()) {
                    this.mDataUsageIgnoreUidListSelfLocked.add(Integer.valueOf(PackageUtil.getUidByPackageName(this.mContext, it.next())));
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void initTrafficStatusMonitorVariable() {
        this.mMonthStartTime = DateUtil.getThisMonthBeginTimeMillis(this.mSimUser.getMonthStart());
        boolean z = true;
        this.mIsDataUsageOverNormalPkg = getCorrectedMonthTotalUsed() - getNormalMonthTotalPackage() >= 0;
        this.mDataUsageTotalPackage = getCurrentMonthTotalPackage();
        this.mIsTotalPackageSetted = this.mDataUsageTotalPackage >= 0;
        this.mIsDataUsageOverLimitOn = this.mIsTotalPackageSetted & this.mSimUser.isDataUsageOverLimitStopNetwork();
        this.mDataUsageTotalPackageWarning = (long) (((float) this.mDataUsageTotalPackage) * this.mSimUser.getDataUsageWarning());
        this.mIsMiSim = MiSimUtil.isMiSimEnable(this.mContext, this.mSimUser.getSlotNum());
        updateTrafficCorrectionTotalLimit();
        if (!this.mIsTotalPackageSetted || !this.mSimUser.isLeisureDataUsageOverLimitWarning()) {
            z = false;
        }
        this.mIsLeisureDataUsageOverLimitOn = z;
        this.mLeisureDataUsageTotal = this.mSimUser.getLeisureDataUsageTotal();
        if (this.mSimUser.isLeisureDataUsageEffective()) {
            this.mLeisureFromTime = this.mSimUser.getLeisureDataUsageFromTime();
            this.mLeisureToTime = this.mSimUser.getLeisureDataUsageToTime();
        }
        this.mIsDailyLimitEnabled = this.mSimUser.getDailyLimitEnabled();
        this.mDailyLimitTraffic = getDailyLimitTraffic();
        this.mOverDailyLimitWarningType = this.mSimUser.getDailyLimitWarningType();
        initDataUsageIgnoreAppList();
        this.mIsTrafficPurchaseAvailable = CooperationManager.isTrafficPurchaseAvailable(this.mContext, this.mSimUser, false);
        this.mRoamingDailyLimitTraffic = this.mSimUser.getRoamingDailyLimitTraffic();
        this.mIsRoamingDailyLimitEnabled = this.mSimUser.getRoamingDailyLimitEnabled();
        this.mRoamingOverLimitOptType = this.mSimUser.getRoamingOverLimitOptType();
        showTrafficInStatusBar();
        initDailyCardInfo();
    }

    /* access modifiers changed from: package-private */
    public void reportSms() {
        Log.i(TAG, "reportSms");
        final String tcSmsReportCache = this.mSimUser.getTcSmsReportCache();
        if (!TextUtils.isEmpty(tcSmsReportCache)) {
            a.a(new Runnable() {
                public void run() {
                    try {
                        JSONArray jSONArray = new JSONArray(tcSmsReportCache);
                        WebApiAccessHelper.reportTrafficCorrectionSms(jSONArray.getString(0), jSONArray.getString(1), jSONArray.getString(2), jSONArray.getString(3), jSONArray.getString(4), jSONArray.getString(5), jSONArray.getString(6), jSONArray.getString(7), jSONArray.getString(8), jSONArray.getString(9));
                        TrafficSimManager.this.mSimUser.setTcSmsReportCache((String) null);
                        Log.i(TrafficSimManager.TAG, "reportSms succeed");
                    } catch (JSONException e) {
                        Log.i(TrafficSimManager.TAG, "report sms exception", e);
                    }
                }
            });
        }
    }

    /* access modifiers changed from: package-private */
    public void resetTrafficPurchaseStatus() {
        Log.i(TAG, "update purchase traffic status true");
        this.mSimUser.setTrafficPurchaseStatus(true);
    }

    /* access modifiers changed from: package-private */
    public void saveCorrectedPkgsAndUsageValues(TrafficUsedStatus trafficUsedStatus, boolean z) {
        if (z) {
            saveCorrectedPkgs(trafficUsedStatus);
            clearAllLimitTime();
        }
        initTrafficStatusMonitorVariable();
        saveCorrectedUsageValues(trafficUsedStatus);
        finishCorrection(true);
        this.mService.broadCastDataUsageUpdated();
    }

    public void saveCorrectionResult(TrafficUsedStatus trafficUsedStatus) {
        if (trafficUsedStatus.getCorrectionType() == 1) {
            this.mSimUser.setTrafficTcResult(trafficUsedStatus.toTrafficString());
            this.mSimUser.setTrafficTcResultCode(trafficUsedStatus.getReturnCode());
            this.mSimUser.saveLastTcUsed(trafficUsedStatus.getUsedTrafficB());
            this.mSimUser.saveLastTcRemain(trafficUsedStatus.getRemainTrafficB());
        } else if (trafficUsedStatus.getCorrectionType() == 2) {
            this.mSimUser.setBillTcResult(trafficUsedStatus.toBillString());
            this.mSimUser.setBillTcResultCode(trafficUsedStatus.getReturnCode());
        }
    }

    /* access modifiers changed from: package-private */
    public void saveLatestCorrectedLeisureDataUsage(long j) {
        this.mSimUser.saveLeisureDataUsageCorrectedValue(j - getCorrectedNormalAndLeisureMonthTotalUsedNoAligned()[1]);
        this.mSimUser.saveLeisureDataUsageCorrectedTime(System.currentTimeMillis());
    }

    /* access modifiers changed from: package-private */
    public void saveLatestCorrectedNormalDataUsage(long j) {
        long j2;
        if (this.mSimUser.isDailyUsedCardEffective()) {
            long normalMonthTotalPackage = getNormalMonthTotalPackage();
            if (j > normalMonthTotalPackage) {
                this.mSimUser.setDailyUsedCardStopNetworkCount((int) ((j - normalMonthTotalPackage) / this.mDailyCardPackage));
                j2 = getNormalTodayDataUsageUsed();
                this.mSimUser.saveCorrectedOffsetValue(j - j2);
                this.mSimUser.saveDataUsageCorrectedTime(System.currentTimeMillis());
            }
        } else if (this.mSimUser.isLeisureDataUsageEffective()) {
            j2 = getCorrectedNormalAndLeisureMonthTotalUsedNoAligned()[0];
            this.mSimUser.saveCorrectedOffsetValue(j - j2);
            this.mSimUser.saveDataUsageCorrectedTime(System.currentTimeMillis());
        }
        j2 = getNormalMonthDataUsageUsed();
        this.mSimUser.saveCorrectedOffsetValue(j - j2);
        this.mSimUser.saveDataUsageCorrectedTime(System.currentTimeMillis());
    }

    public boolean startCorrection(final boolean z, final boolean z2, final int i) {
        if (!this.mTrafficCorrection.isFinished()) {
            return false;
        }
        if (!this.mSimUser.isSupportCorrection()) {
            return true;
        }
        this.mIsSimLocationError = false;
        this.mIsTcDiagnostic = false;
        if (!z) {
            showCorrectionStartedToast();
            this.mSimUser.saveWebCorrectionStatusRefreshTime(0);
        }
        a.a(new Runnable() {
            public void run() {
                boolean isSupportSmsCorrection = TelephonyUtil.isSupportSmsCorrection(TrafficSimManager.this.mSimUser.getOperator());
                boolean access$100 = (z || !isSupportSmsCorrection) ? false : TrafficSimManager.this.updateTrafficCorrectionEngine();
                if (z2 && !access$100 && isSupportSmsCorrection) {
                    TrafficSimManager.this.showCorrectionFailedToast();
                } else if (TrafficSimManager.this.mSimUser.isCorrectionEffective()) {
                    TrafficSimManager.this.startTrafficCorrection(z, TrafficSimManager.this.getNormalMonthDataUsageUsed(), i);
                    Log.i(TrafficSimManager.TAG, String.format("LRL startTrafficCorrection isBackground %b, isCareUpdateResult %b, type%d", new Object[]{Boolean.valueOf(z), Boolean.valueOf(z2), Integer.valueOf(i)}));
                }
            }
        });
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean startCorrectionDiagnostic() {
        this.mIsTcDiagnostic = true;
        return this.mTrafficCorrection.startCorrection(false, (Map<String, String>) null);
    }

    /* access modifiers changed from: package-private */
    public void updateAutoCorrectionConfig() {
        if (this.mSimUser.isSimInserted() && this.mSimUser.hasImsi() && !this.mSimUser.isOversea()) {
            miui.util.Log.getFullLogger().info(TAG, this.mSimUser.toString());
            synchronized (this.mUpdateAutoCorrectionLock) {
                if (this.mSimUser.isDataRoaming()) {
                    this.mSimUser.toggleDataUsageAutoCorrection(false);
                    this.mSimUser.setDataRoamingStopUpdateTime(0);
                    this.mSimUser.setDataRoamingStopChanged(true);
                } else if (this.mSimUser.getDataRoamingStopChanged()) {
                    this.mSimUser.setDataRoamingStopUpdateTime(System.currentTimeMillis());
                    this.mSimUser.toggleDataUsageAutoCorrection(true);
                    this.mSimUser.setDataRoamingStopChanged(false);
                }
            }
            Log.Facade fullLogger = miui.util.Log.getFullLogger();
            fullLogger.info(TAG, "Correction : " + this.mSimUser.isCorrectionEffective());
        }
    }

    /* access modifiers changed from: package-private */
    public void updateRoamingBeginTime() {
        boolean z;
        SimUserInfo simUserInfo;
        if (this.mSimUser.isSimInserted()) {
            if (!this.mSimUser.hasImsi() || !this.mSimUser.isDataRoaming() || this.mSimUser.getRoamingNetworkState()) {
                this.mSimUser.setRoamingBeginTime(0);
                simUserInfo = this.mSimUser;
                z = false;
            } else {
                android.util.Log.i(TAG, "updateRoamingBeginTime : " + System.currentTimeMillis());
                this.mSimUser.setRoamingBeginTime(System.currentTimeMillis());
                simUserInfo = this.mSimUser;
                z = true;
            }
            simUserInfo.setRoamingNetworkState(z);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean updateTrafficCorrectionEngine(String str, String str2, String str3) {
        SimUserInfo simUserInfo = this.mSimUser;
        if (simUserInfo == null || !simUserInfo.isOperatorSetted()) {
            return false;
        }
        android.util.Log.i(TAG, "diagnostic update correction engine");
        return this.mTrafficCorrection.updateSMSTemplate(str, str2, str3);
    }

    /* access modifiers changed from: package-private */
    public void updateTrafficCorrectionTotalLimit() {
        this.mTrafficCorrection.setTotalLimit(getCurrentMonthTotalPackage());
    }
}
