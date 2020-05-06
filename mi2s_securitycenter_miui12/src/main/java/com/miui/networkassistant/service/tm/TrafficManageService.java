package com.miui.networkassistant.service.tm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import b.b.c.c.a.a;
import b.b.c.h.f;
import b.b.c.j.B;
import b.b.c.j.g;
import b.b.o.g.c;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.miui.maml.data.VariableNames;
import com.miui.networkassistant.config.CommonConfig;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.config.DataUsageIgnoreAppListConfig;
import com.miui.networkassistant.config.SharedPreferenceHelper;
import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.dual.DualSimInfoManager;
import com.miui.networkassistant.dual.SimCardHelper;
import com.miui.networkassistant.firewall.BackgroundPolicyService;
import com.miui.networkassistant.model.TrafficUsedStatus;
import com.miui.networkassistant.service.IAppMonitorBinder;
import com.miui.networkassistant.service.ISharedPreBinder;
import com.miui.networkassistant.service.ISharedPreBinderListener;
import com.miui.networkassistant.service.ITrafficCornBinder;
import com.miui.networkassistant.service.ITrafficCornBinderListener;
import com.miui.networkassistant.service.ITrafficManageBinder;
import com.miui.networkassistant.traffic.correction.ITrafficCorrection;
import com.miui.networkassistant.traffic.correction.WebCorrectionManager;
import com.miui.networkassistant.traffic.statistic.LeisureTrafficHelper;
import com.miui.networkassistant.traffic.statistic.MiServiceFrameworkHelper;
import com.miui.networkassistant.traffic.statistic.PreSetGroup;
import com.miui.networkassistant.utils.DateUtil;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.NotificationUtil;
import com.miui.networkassistant.utils.TelephonyUtil;
import com.miui.networkassistant.utils.ToastUtil;
import com.miui.networkassistant.utils.TrafficUpdateUtil;
import com.miui.networkassistant.xman.XmanHelper;
import com.miui.networkassistant.xman.XmanShareReceiver;
import com.miui.networkassistant.zman.ZmanHelper;
import com.miui.networkassistant.zman.ZmanShareReceiver;
import com.miui.securitycenter.R;
import com.miui.support.provider.MiuiSettingsCompat$SettingsCloudData;
import com.xiaomi.stat.MiStat;
import java.util.HashMap;
import java.util.Map;

public class TrafficManageService extends Service {
    private static final int CMCC_NET_AUTO_CORRECTION = 3;
    private static final int DAILY_AUTO_CORRECTION = 0;
    private static final int MAX_RETRY_TIME = 10;
    private static final int MAX_TRAFFIC_RATE = 500000;
    static final int MSG_CANCEL_WIFI_TO_MOBILE = 98;
    static final int MSG_DEVICE_PROVISIONED_CHANGED = 32;
    static final int MSG_FORCE_CHECK_DAILY_LIMIT_STATUS = 64;
    static final int MSG_FORCE_CHECK_LOCK_SCREEN_STATUS = 66;
    static final int MSG_FORCE_CHECK_ROAMING_DAILY_LIMIT_STATUS = 65;
    static final int MSG_FORCE_CHECK_TETHERING_SETTING_STATUS = 66;
    static final int MSG_FORCE_CHECK_TRAFFIC_STATUS = 2;
    static final int MSG_INIT_SIM_STATE = 80;
    private static final int MSG_TRACK_USER_DATA = 48;
    static final int MSG_TRAFFIC_AUTO_CORRECTION_LAUNCH = 19;
    static final int MSG_TRAFFIC_CORRECTION_FAILED = 18;
    static final int MSG_TRAFFIC_CORRECTION_SAVE_PKG = 20;
    static final int MSG_TRAFFIC_CORRECTION_STARTED = 16;
    static final int MSG_TRAFFIC_CORRECTION_SUCCEED = 17;
    private static final int MSG_UPDATE_TC_ENGINE = 49;
    static final int MSG_UPDATE_TRAFFIC_STATS_DAILY = 21;
    static final int MSG_UPDATE_TRAFFIC_STATUS_MONITOR = 1;
    static final int MSG_UPLOAD_DATA_USAGE_DAILY = 22;
    static final int MSG_WIFI_TO_MOBILE = 96;
    static final int MSG_WIFI_TO_MOBILE_DELAY = 97;
    private static final int NET_AUTO_CORRECTION = 1;
    private static final int PURCHASE_SUCCESS_AUTO_CORRECTION = 2;
    private static final String TAG = "TrafficManageService";
    private static final int WIFI_TO_MOBILE_DELAY = 5;
    private static final int WIFI_TO_MOBILE_RANGE = 4;
    /* access modifiers changed from: private */
    public int mActiveSlotNum = 0;
    /* access modifiers changed from: private */
    public TrackAnalyticsManager mAnalyticsManager;
    /* access modifiers changed from: private */
    public AppMonitor[] mAppMonitor;
    private BroadcastReceiver mAutoCorrectionReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.i(TrafficManageService.TAG, "mina mAutoCorrectionReceiver onReceive");
            long todayTimeMillis = DateUtil.getTodayTimeMillis();
            boolean access$2100 = TrafficManageService.this.checkSmsShouldAutoCorrection(0, todayTimeMillis);
            if (access$2100) {
                Message obtainMessage = TrafficManageService.this.mHandler.obtainMessage(19);
                obtainMessage.arg1 = 0;
                obtainMessage.arg2 = 0;
                TrafficManageService.this.mHandler.sendMessage(obtainMessage);
            }
            if (DeviceUtil.IS_DUAL_CARD && TrafficManageService.this.checkSmsShouldAutoCorrection(1, todayTimeMillis)) {
                long j = access$2100 ? 550000 : 0;
                Message obtainMessage2 = TrafficManageService.this.mHandler.obtainMessage(19);
                obtainMessage2.arg1 = 1;
                obtainMessage2.arg2 = 0;
                TrafficManageService.this.mHandler.sendMessageDelayed(obtainMessage2, j);
            }
        }
    };
    /* access modifiers changed from: private */
    public final Handler mBackgroundHandler;
    private Handler.Callback mBackgroundHandlerCallback = new Handler.Callback() {
        public boolean handleMessage(Message message) {
            TrafficSimManager trafficSimManager;
            String str;
            int i = message.arg1;
            int i2 = message.what;
            if (i2 != 1) {
                if (i2 == 2) {
                    trafficSimManager = TrafficManageService.this.mTrafficManagers[i];
                } else if (i2 == 21) {
                    TrafficManageService.this.mTrafficManagers[0].initTrafficStatusMonitorVariable();
                    TrafficManageService.this.mTrafficManagers[0].forceUpdateTraffic();
                    TrafficManageService.this.broadCastDataUsageUpdated();
                    TrafficManageService.this.mMonthReportTrafficManager.uploadTrafficDataDaily(TrafficManageService.this.mTrafficManagers);
                    if (DeviceUtil.IS_DUAL_CARD) {
                        TrafficManageService.this.mTrafficManagers[1].initTrafficStatusMonitorVariable();
                        TrafficManageService.this.mTrafficManagers[1].forceUpdateTraffic();
                    }
                } else if (i2 == 22) {
                    TrafficManageService.this.mMonthReportTrafficManager.uploadTrafficDataDaily(TrafficManageService.this.mTrafficManagers);
                } else if (i2 == 48) {
                    TrafficManageService.this.mAnalyticsManager.trackAnalyticsWeekly(TrafficManageService.this.mActiveSlotNum);
                } else if (i2 != 49) {
                    switch (i2) {
                        case 64:
                            TrafficManageService.this.mTrafficManagers[i].initTrafficStatusMonitorVariable();
                            TrafficManageService.this.mTrafficManagers[i].clearDailyLimitTime();
                            trafficSimManager = TrafficManageService.this.mTrafficManagers[i];
                            break;
                        case 65:
                            TrafficManageService.this.mTrafficManagers[i].initTrafficStatusMonitorVariable();
                            TrafficManageService.this.mTrafficManagers[i].clearRoamingDailyLimitTime();
                            trafficSimManager = TrafficManageService.this.mTrafficManagers[i];
                            break;
                        case 66:
                            TrafficManageService.this.mTetherStatsManager.initTetheringStatus();
                            break;
                        default:
                            switch (i2) {
                                case 96:
                                    if (f.i(TrafficManageService.this)) {
                                        long unused = TrafficManageService.this.mPreByte = TrafficStats.getTotalTxBytes() + TrafficStats.getTotalRxBytes();
                                        TrafficManageService.this.mBackgroundHandler.sendEmptyMessageDelayed(97, 4000);
                                        break;
                                    }
                                    break;
                                case 97:
                                    if (!f.i(TrafficManageService.this)) {
                                        str = "wifi2mobile: Mobile network does not connected";
                                    } else {
                                        long totalTxBytes = TrafficStats.getTotalTxBytes() + TrafficStats.getTotalRxBytes();
                                        if (totalTxBytes - TrafficManageService.this.mPreByte > 2000000) {
                                            NotificationUtil.showNetworkChangedNotify(TrafficManageService.this);
                                            boolean unused2 = TrafficManageService.this.wifiToMobileShowing = true;
                                        }
                                        str = "wifi2mobile: deltaByte:" + (totalTxBytes - TrafficManageService.this.mPreByte);
                                    }
                                    Log.i(TrafficManageService.TAG, str);
                                    break;
                                case 98:
                                    if (!f.i(TrafficManageService.this)) {
                                        NotificationUtil.cancelNetworkChangedNotify(TrafficManageService.this);
                                        boolean unused3 = TrafficManageService.this.wifiToMobileShowing = false;
                                        break;
                                    }
                                    break;
                            }
                    }
                } else {
                    TrafficManageService.this.checkAllTrafficCorrectionEngineUpdate();
                }
                trafficSimManager.checkTrafficStatus();
            } else {
                TrafficManageService.this.mTrafficManagers[i].initTrafficStatusMonitorVariable();
                TrafficManageService.this.mTrafficManagers[i].clearAllLimitTime();
                TrafficManageService.this.mTrafficManagers[i].checkTrafficStatus();
                if (i == TrafficManageService.this.mActiveSlotNum) {
                    TrafficManageService.this.mLockScreenManager.initLockScreenMonitor();
                }
                TrafficManageService.this.broadCastDataUsageUpdated();
            }
            return true;
        }
    };
    /* access modifiers changed from: private */
    public CommonConfig mCommonConfig;
    /* access modifiers changed from: private */
    public int mCurrentUserIndex;
    private ContentObserver mDeviceProvisionedObserver = new ContentObserver(this.mHandler) {
        public void onChange(boolean z) {
            Log.i(TrafficManageService.TAG, "mina mDeviceProvisionedObserver onChange");
            if (TrafficManageService.this.mHandler.hasMessages(32)) {
                TrafficManageService.this.mHandler.removeMessages(32);
            }
            TrafficManageService.this.mHandler.sendEmptyMessageDelayed(32, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        }
    };
    private BroadcastReceiver mExtraNetworkReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (!DeviceUtil.IS_INTERNATIONAL_BUILD) {
                Log.i(TrafficManageService.TAG, "mExtraNetworkReceiver tag = " + intent.getAction());
                if (Constants.System.ACTION_NETWORK_BLOCKED.equals(intent.getAction())) {
                    if (TrafficManageService.this.mNetworkCheckStateManager != null) {
                        TrafficManageService.this.mNetworkCheckStateManager.networkBlocked();
                    }
                } else if (Constants.System.ACTION_NETWORK_CONNECTED.equals(intent.getAction()) && TrafficManageService.this.mNetworkCheckStateManager != null) {
                    TrafficManageService.this.mNetworkCheckStateManager.networkConnected();
                }
            }
        }
    };
    Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            int i = message.arg1;
            int i2 = message.what;
            if (i2 != 32) {
                if (i2 != 66) {
                    if (i2 != 80) {
                        switch (i2) {
                            case 16:
                            case 20:
                                ToastUtil.makeToastText(TrafficManageService.this.getApplicationContext(), i, message.arg2);
                                return;
                            case 17:
                                ToastUtil.showCorrectionSucceed(TrafficManageService.this.getApplicationContext(), i, R.string.traffic_correction_success);
                                return;
                            case 18:
                                ToastUtil.makeToastText(TrafficManageService.this.getApplicationContext(), i, (int) R.string.traffic_correction_failed);
                                return;
                            case 19:
                                TrafficManageService.this.handleAutoCorrectionMsg(i, message.arg2);
                                return;
                            default:
                                return;
                        }
                    } else {
                        TrafficManageService.this.initSim();
                    }
                } else if (i != TrafficManageService.this.mActiveSlotNum) {
                    return;
                }
                TrafficManageService.this.mLockScreenManager.initLockScreenMonitor();
                return;
            }
            if (DeviceUtil.IS_DUAL_CARD) {
                TrafficManageService.this.updateActiveSlotNum();
            }
            boolean unused = TrafficManageService.this.mIsDeviceProvisioned = true;
        }
    };
    private final HandlerThread mHandlerThread = new HandlerThread(TAG);
    /* access modifiers changed from: private */
    public boolean mIsDeviceProvisioned;
    /* access modifiers changed from: private */
    public LockScreenManager mLockScreenManager;
    private BroadcastReceiver mLockScreenReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            TrafficManageService.this.mLockScreenManager.onLockScreenChange(intent);
            TrafficManageService.this.updateNormalTotalPackageSetted(intent);
            if (TrafficManageService.this.mNetworkCheckStateManager != null) {
                TrafficManageService.this.mNetworkCheckStateManager.onLockScreenChange(intent);
            }
        }
    };
    private final ContentObserver mMiSimCloudDataObserver = new ContentObserver(this.mHandler) {
        public void onChange(boolean z) {
            Log.i(TrafficManageService.TAG, "mMiSimCloudDataObserver change");
            TrafficManageService.this.mCommonConfig.setMiSimCloudData(MiuiSettingsCompat$SettingsCloudData.a(TrafficManageService.this.getApplicationContext().getContentResolver(), "MiCardActivate1", MiStat.Param.CONTENT, (String) null));
        }
    };
    private final ContentObserver mMobileDataEnableObserver = new ContentObserver(this.mHandler) {
        public void onChange(boolean z) {
            Log.i(TrafficManageService.TAG, "mMobileDataEnableObserver");
            if (f.e(TrafficManageService.this.getApplicationContext())) {
                if (!(Settings.Secure.getInt(TrafficManageService.this.getContentResolver(), Constants.System.MOBILE_POLICY, 1) == 1)) {
                    Settings.Secure.putInt(TrafficManageService.this.getContentResolver(), Constants.System.MOBILE_POLICY, 1);
                }
                TrafficManageService.this.initInternationalRoaming();
            }
            NotificationUtil.cancelDataUsageOverLimit(TrafficManageService.this.getApplicationContext());
            NotificationUtil.cancelOpenRoamingWhiteListNotify(TrafficManageService.this.getApplicationContext());
        }
    };
    /* access modifiers changed from: private */
    public int mMobileDataPolicy;
    private final ContentObserver mMobileDataPolicyObserver = new ContentObserver(this.mHandler) {
        public void onChange(boolean z) {
            TrafficManageService trafficManageService = TrafficManageService.this;
            int unused = trafficManageService.mMobileDataPolicy = Settings.Secure.getInt(trafficManageService.getContentResolver(), Constants.System.MOBILE_POLICY, 1);
        }
    };
    /* access modifiers changed from: private */
    public DataUsageReportManager mMonthReportTrafficManager;
    /* access modifiers changed from: private */
    public NetworkCheckStateManager mNetworkCheckStateManager;
    private BroadcastReceiver mNetworkConnectivityReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            XmanHelper.checkXmanCloudDataAsync(TrafficManageService.this.getApplicationContext());
            ZmanHelper.checkZmanCloudDataAsync(TrafficManageService.this.getApplicationContext());
            if (DeviceUtil.IS_DUAL_CARD) {
                TrafficManageService.this.updateActiveSlotNum();
                Log.i(TrafficManageService.TAG, String.format("update mEffectiveSlotNum:%d", new Object[]{Integer.valueOf(TrafficManageService.this.mActiveSlotNum)}));
            }
            f.a c2 = f.c(context);
            Log.i(TrafficManageService.TAG, "mina connectivity updated : " + c2);
            if (c2 == f.a.WifiConnected) {
                TrafficManageService.this.checkCachedTcSmsReport();
                TrafficManageService.this.mPurchaseSmsManager.checkPurchaseSmsNumberWhiteList();
                TrafficManageService.this.startAutoCorrection();
                TrafficManageService.this.postUpdateTrafficCorrectionEngine();
                TrafficManageService.this.postTrackUserDataDaily();
            } else if (c2 == f.a.MobileConnected) {
                TrafficManageService.this.startCmccAutoCorrection();
            }
            TrafficManageService.this.mMonthReportTrafficManager.trackNetworkStateAnalytics(c2);
            TrafficManageService.this.mTrafficManagers[TrafficManageService.this.mActiveSlotNum].updateRoamingBeginTime();
            if (TrafficManageService.this.mNetworkCheckStateManager != null) {
                TrafficManageService.this.mNetworkCheckStateManager.networkChanged();
            }
            TrafficManageService.this.checkMiMobileConfig();
            TrafficManageService.this.checkAutoCorrectionConfig();
        }
    };
    private BroadcastReceiver mPackageReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            int intExtra = intent.getIntExtra(Constants.System.EXTRA_USER_HANDLE, B.e());
            Log.i(TrafficManageService.TAG, "mina mPackageReceiver " + intExtra);
            if (intExtra != B.e()) {
                TrafficManageService.this.mAppMonitor[intExtra != 999 ? B.b(intExtra) : 0].onPackageChanged(intent);
            }
        }
    };
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            if (TrafficManageService.this.mNetworkCheckStateManager != null) {
                TrafficManageService.this.mNetworkCheckStateManager.onSignalStrengthChanged(signalStrength);
            }
        }
    };
    /* access modifiers changed from: private */
    public long mPreByte;
    /* access modifiers changed from: private */
    public PurchaseSmsManager mPurchaseSmsManager;
    private BroadcastReceiver mPurchaseSuccessReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.i(TrafficManageService.TAG, "mina mPurchaseSuccessReceiver " + intent.toString());
            try {
                int parseInt = Integer.parseInt(intent.getStringExtra("slotId"));
                if (parseInt >= 0 && parseInt <= 1) {
                    TrafficManageService.this.mTrafficManagers[parseInt].resetTrafficPurchaseStatus();
                    TrafficManageService.this.mCommonConfig.setSmsNumberReceiverUpdateTime(0);
                }
            } catch (NumberFormatException e) {
                Log.i(TrafficManageService.TAG, "mPurchaseSuccessReceiver ", e);
            }
        }
    };
    private BroadcastReceiver mRefreshDataUsageDailyReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.i(TrafficManageService.TAG, "mina mRefreshDataUsageDailyReceiver");
            TrafficManageService.this.mBackgroundHandler.sendEmptyMessage(21);
        }
    };
    private int mRetryTime = 0;
    private BroadcastReceiver mScNetworkStatusReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean booleanExtra = intent.getBooleanExtra("extra_network_status", false);
            Log.i(TrafficManageService.TAG, "mina mScNetworkStatusReceiver : " + booleanExtra);
            if (booleanExtra) {
                TrafficManageService.this.checkMiMobileConfig();
                XmanHelper.checkXmanCloudDataAsync(context.getApplicationContext());
                ZmanHelper.checkZmanCloudDataAsync(context.getApplicationContext());
            }
        }
    };
    /* access modifiers changed from: private */
    public SimCardHelper mSimCardHelper;
    private DualSimInfoManager.ISimInfoChangeListener mSimInfoChangeListener = new DualSimInfoManager.ISimInfoChangeListener() {
        public void onSubscriptionsChanged() {
            Log.i(TrafficManageService.TAG, "ISimInfoChangeListener onChange");
            TrafficManageService.this.postInitSimMsgDelayed();
            TrafficManageService.this.updateRoamingStateChanged();
        }
    };
    private BroadcastReceiver mSimStateDataSlotReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.i(TrafficManageService.TAG, "mina mSimStateDataSlotReceiver sim data slot updated" + intent.toString());
            TrafficManageService.this.updateActiveSlotNum();
            TrafficManageService.this.mTrafficManagers[TrafficManageService.this.mActiveSlotNum].checkActiveSlotTraffic();
            TrafficManageService.this.mLockScreenManager.initLockScreenMonitor();
            TrafficManageService.this.mTrafficManagers[TrafficManageService.this.mActiveSlotNum].initTrafficStatusMonitorVariable();
        }
    };
    private BroadcastReceiver mSimStateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.i(TrafficManageService.TAG, "mina mSimStateReceiver sim state updated" + intent.toString());
            TrafficManageService.this.postInitSimMsgDelayed();
            TrafficManageService.this.cancelNotificationWhenSimChanged();
        }
    };
    private BroadcastReceiver mSmsReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (TrafficManageService.this.mCommonConfig.getSmsNumberReceiverUpdateTime() < DateUtil.getTodayTimeMillis()) {
                Log.i(TrafficManageService.TAG, "mina mSmsReceiver");
                int slotIdFromIntent = TrafficManageService.this.mPurchaseSmsManager.getSlotIdFromIntent(intent);
                boolean z = true;
                if (slotIdFromIntent < 0 || slotIdFromIntent > 1) {
                    z = false;
                }
                if (TrafficManageService.this.mTrafficManagers[slotIdFromIntent].mSimUser.getTrafficPurchaseStatus()) {
                    TrafficManageService.this.mCommonConfig.setSmsNumberReceiverUpdateTime(System.currentTimeMillis());
                    TrafficManageService.this.mTrafficManagers[slotIdFromIntent].mSimUser.setTrafficPurchaseStatus(false);
                    if (z && TrafficManageService.this.mTrafficManagers[slotIdFromIntent].mTrafficCorrection.isFinished() && TrafficManageService.this.mPurchaseSmsManager.checkContainReceiveNumber(intent)) {
                        Log.i(TrafficManageService.TAG, "mina traffic correction by sms receiver");
                        TrafficManageService.this.mTrafficManagers[slotIdFromIntent].mSimUser.setPackageChangeUpdateTime(0);
                        Message obtainMessage = TrafficManageService.this.mHandler.obtainMessage(19);
                        obtainMessage.arg1 = slotIdFromIntent;
                        obtainMessage.arg2 = 2;
                        TrafficManageService.this.mHandler.sendMessageDelayed(obtainMessage, 300000);
                    }
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public TetherStatsManager mTetherStatsManager;
    /* access modifiers changed from: private */
    public TrafficCornBinder[] mTrafficCornBinders;
    private TrafficManageBinder mTrafficManageBinder;
    /* access modifiers changed from: private */
    public TrafficSimManager[] mTrafficManagers;
    private BroadcastReceiver mTrafficStatsReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.i(TrafficManageService.TAG, "mina mTrafficStatsReceiver onReceive");
            TrafficManageService.this.broadCastDataUsageUpdated();
            TrafficManageService.this.mTrafficManagers[TrafficManageService.this.mActiveSlotNum].checkTrafficStatus();
            TrafficManageService.this.mTetherStatsManager.checkTetheringTrafficStatus();
            TrafficManageService.this.mTrafficManagers[TrafficManageService.this.mActiveSlotNum].checkTrafficSettingAndSendNotification();
            TrafficManageService.this.mAnalyticsManager.trackAnalyticDaily(TrafficManageService.this.mActiveSlotNum);
        }
    };
    private BroadcastReceiver mUserSwitchReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.i(TrafficManageService.TAG, "mina mUserSwitchReceiver onReceive");
            int unused = TrafficManageService.this.mCurrentUserIndex = B.b(B.c());
            TrafficManageService.this.mAppMonitor[TrafficManageService.this.mCurrentUserIndex].initData(B.c());
        }
    };
    private BroadcastReceiver mWifiApStateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            int intExtra = intent.getIntExtra(VariableNames.WIFI_STATE, 0);
            if (intExtra == 11) {
                TrafficManageService.this.mTetherStatsManager.initTetheringStatus(false);
            } else if (intExtra == 13) {
                TrafficManageService.this.mTetherStatsManager.initTetheringStatus(true);
            }
        }
    };
    private BroadcastReceiver mWifiNetworkStatusReceiver = new BroadcastReceiver() {
        private NetworkInfo.State lastState = NetworkInfo.State.DISCONNECTED;

        public void onReceive(Context context, Intent intent) {
            if ("android.net.wifi.STATE_CHANGE".equals(intent.getAction())) {
                Parcelable parcelableExtra = intent.getParcelableExtra("networkInfo");
                boolean z = false;
                NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getNetworkInfo(0);
                if (!(parcelableExtra == null || networkInfo == null)) {
                    NetworkInfo.State state = ((NetworkInfo) parcelableExtra).getState();
                    boolean z2 = state == NetworkInfo.State.DISCONNECTED;
                    if (this.lastState == NetworkInfo.State.CONNECTED) {
                        z = true;
                    }
                    z = networkInfo.isAvailable() & z & z2 & f.m(context) & TrafficManageService.this.mSimCardHelper.isSimInserted() & f.e(context);
                    this.lastState = state;
                }
                if (z) {
                    TrafficManageService.this.postWifiToMobile();
                } else {
                    TrafficManageService.this.cancelWifiToMobile();
                }
            }
        }
    };
    private XmanShareReceiver mXmanShareReceiver = new XmanShareReceiver();
    private ZmanShareReceiver mZmanShareReceiver = new ZmanShareReceiver();
    /* access modifiers changed from: private */
    public boolean wifiToMobileShowing = false;

    private class SharedPreBinder extends ISharedPreBinder.Stub {
        private final SharedPreferenceHelper mSPHelper;

        public SharedPreBinder(String str) {
            this.mSPHelper = SharedPreferenceHelper.getInstance(TrafficManageService.this.getApplicationContext(), str);
        }

        public void attachBinderListener(ISharedPreBinderListener iSharedPreBinderListener) {
            this.mSPHelper.attachBinderListener(iSharedPreBinderListener);
        }

        public boolean getBoolean(String str, boolean z) {
            return this.mSPHelper.load(str, z);
        }

        public float getFloat(String str, float f) {
            return this.mSPHelper.load(str, f);
        }

        public int getInt(String str, int i) {
            return this.mSPHelper.load(str, i);
        }

        public long getLong(String str, long j) {
            return this.mSPHelper.load(str, j);
        }

        public String getString(String str, String str2) {
            return this.mSPHelper.load(str, str2);
        }

        public boolean putBoolean(String str, boolean z) {
            return this.mSPHelper.save(str, z);
        }

        public boolean putFloat(String str, float f) {
            return this.mSPHelper.save(str, f);
        }

        public boolean putInt(String str, int i) {
            return this.mSPHelper.save(str, i);
        }

        public boolean putLong(String str, long j) {
            return this.mSPHelper.save(str, j);
        }

        public boolean putString(String str, String str2) {
            return this.mSPHelper.save(str, str2);
        }
    }

    private class TrafficCornBinder extends ITrafficCornBinder.Stub {
        private RemoteCallbackList<ITrafficCornBinderListener> mListeners;
        private ITrafficCorrection mTrafficCorrection;
        private ITrafficCorrection.TrafficCorrectionListener mTrafficCorrectionListener;

        private TrafficCornBinder() {
            this.mTrafficCorrectionListener = new ITrafficCorrection.TrafficCorrectionListener() {
                public void onTrafficCorrected(TrafficUsedStatus trafficUsedStatus) {
                    TrafficCornBinder.this.onTrafficCorrected(trafficUsedStatus);
                }
            };
            this.mListeners = new RemoteCallbackList<>();
        }

        /* access modifiers changed from: private */
        public void onTrafficCorrected(TrafficUsedStatus trafficUsedStatus) {
            synchronized (this.mListeners) {
                int beginBroadcast = this.mListeners.beginBroadcast();
                while (beginBroadcast > 0) {
                    beginBroadcast--;
                    try {
                        this.mListeners.getBroadcastItem(beginBroadcast).onTrafficCorrected(trafficUsedStatus);
                    } catch (RemoteException e) {
                        Log.i(TrafficManageService.TAG, "onTrafficCorrected exception", e);
                    }
                }
                this.mListeners.finishBroadcast();
            }
        }

        public Map getBrands(String str) {
            return this.mTrafficCorrection.getBrands(str);
        }

        public Map getCities(int i) {
            return this.mTrafficCorrection.getCities(i);
        }

        public Map getInstructions(int i) {
            return this.mTrafficCorrection.getInstructions(i);
        }

        public Map getOperators() {
            return this.mTrafficCorrection.getOperators();
        }

        public int getProvinceCodeByCityCode(int i) {
            return this.mTrafficCorrection.getProvinceCodeByCityCode(i);
        }

        public Map getProvinces() {
            return this.mTrafficCorrection.getProvinces();
        }

        public int getTcType() {
            return this.mTrafficCorrection.getTcType();
        }

        public boolean isConfigUpdated() {
            return this.mTrafficCorrection.isConfigUpdated();
        }

        public boolean isFinished() {
            return this.mTrafficCorrection.isFinished();
        }

        public void registerLisener(ITrafficCornBinderListener iTrafficCornBinderListener) {
            synchronized (this.mListeners) {
                this.mListeners.register(iTrafficCornBinderListener);
            }
        }

        public void setTrafficCorrection(ITrafficCorrection iTrafficCorrection) {
            ITrafficCorrection iTrafficCorrection2 = this.mTrafficCorrection;
            if (iTrafficCorrection2 != iTrafficCorrection) {
                if (iTrafficCorrection2 != null) {
                    iTrafficCorrection2.unRegisterLisener(this.mTrafficCorrectionListener);
                }
                this.mTrafficCorrection = iTrafficCorrection;
                this.mTrafficCorrection.registerLisener(this.mTrafficCorrectionListener);
            }
        }

        public void unRegisterLisener(ITrafficCornBinderListener iTrafficCornBinderListener) {
            synchronized (this.mListeners) {
                this.mListeners.unregister(iTrafficCornBinderListener);
            }
        }
    }

    private class TrafficManageBinder extends ITrafficManageBinder.Stub {
        private HashMap<String, SharedPreBinder> mSharedPreBinderMap;

        private TrafficManageBinder() {
            this.mSharedPreBinderMap = new HashMap<>();
        }

        public void applyCorrectedPkgsAndUsageValues(TrafficUsedStatus trafficUsedStatus, boolean z, int i) {
            TrafficManageService.this.mTrafficManagers[i].saveCorrectedPkgsAndUsageValues(trafficUsedStatus, z);
        }

        public void clearDataUsageIgnore(int i) {
            DataUsageIgnoreAppListConfig dataUsageIgnoreAppListConfig = TrafficManageService.this.mTrafficManagers[i].mIgnoreAppListConfig;
            if (dataUsageIgnoreAppListConfig != null) {
                dataUsageIgnoreAppListConfig.clear();
            }
        }

        public void forceCheckDailyLimitStatus(int i) {
            Message obtainMessage = TrafficManageService.this.mBackgroundHandler.obtainMessage(64);
            obtainMessage.arg1 = i;
            TrafficManageService.this.mBackgroundHandler.sendMessage(obtainMessage);
        }

        public void forceCheckLockScreenStatus(int i) {
            Message obtainMessage = TrafficManageService.this.mHandler.obtainMessage(66);
            obtainMessage.arg1 = i;
            TrafficManageService.this.mHandler.sendMessage(obtainMessage);
        }

        public void forceCheckRoamingDailyLimitStatus(int i) {
            Message obtainMessage = TrafficManageService.this.mBackgroundHandler.obtainMessage(65);
            obtainMessage.arg1 = i;
            TrafficManageService.this.mBackgroundHandler.sendMessage(obtainMessage);
        }

        public void forceCheckTethingSettingStatus() {
            TrafficManageService.this.mBackgroundHandler.sendMessage(TrafficManageService.this.mBackgroundHandler.obtainMessage(66));
        }

        public void forceCheckTrafficStatus(int i) {
            Message obtainMessage = TrafficManageService.this.mBackgroundHandler.obtainMessage(2);
            obtainMessage.arg1 = i;
            TrafficManageService.this.mBackgroundHandler.sendMessage(obtainMessage);
        }

        public int getActiveSlotNum() {
            TrafficManageService.this.updateActiveSlotNum();
            return TrafficManageService.this.mActiveSlotNum;
        }

        public IAppMonitorBinder getAppMonitorBinder() {
            return TrafficManageService.this.mAppMonitor[B.b(B.a())].getBinder();
        }

        public long[] getCorrectedNormalAndLeisureMonthTotalUsed(int i) {
            if (TrafficManageService.this.mTrafficManagers[i] != null) {
                return TrafficManageService.this.mTrafficManagers[i].getCorrectedNormalAndLeisureMonthTotalUsed();
            }
            return null;
        }

        public long getCorrectedNormalMonthDataUsageUsed(int i) {
            if (TrafficManageService.this.mTrafficManagers[i] != null) {
                return TrafficManageService.this.mTrafficManagers[i].mSimUser.isLeisureDataUsageEffective() ? TrafficManageService.this.mTrafficManagers[i].getCorrectedNormalAndLeisureMonthTotalUsed()[0] : TrafficManageService.this.mTrafficManagers[i].getCorrectedNormalMonthTotalUsed();
            }
            return 0;
        }

        public long getCurrentMonthTotalPackage(int i) {
            if (TrafficManageService.this.mTrafficManagers[i] != null) {
                return TrafficManageService.this.mTrafficManagers[i].getCurrentMonthTotalPackage();
            }
            return 0;
        }

        public int getIgnoreAppCount(int i) {
            return TrafficManageService.this.mTrafficManagers[i].mIgnoreAppListConfig.getIgnoreList().size();
        }

        public long getNormalTodayDataUsageUsed(int i) {
            if (TrafficManageService.this.mTrafficManagers[i] != null) {
                return TrafficManageService.this.mTrafficManagers[i].getNormalTodayDataUsageUsed();
            }
            return 0;
        }

        public ISharedPreBinder getSharedPreBinder(String str, ISharedPreBinderListener iSharedPreBinderListener) {
            SharedPreBinder sharedPreBinder;
            synchronized (this.mSharedPreBinderMap) {
                sharedPreBinder = this.mSharedPreBinderMap.get(str);
                if (sharedPreBinder == null) {
                    sharedPreBinder = new SharedPreBinder(str);
                    this.mSharedPreBinderMap.put(str, sharedPreBinder);
                }
                sharedPreBinder.attachBinderListener(iSharedPreBinderListener);
            }
            return sharedPreBinder;
        }

        public long getTodayDataUsageUsed(int i) {
            if (TrafficManageService.this.mTrafficManagers[i] == null) {
                return 0;
            }
            SimUserInfo simUserInfo = TrafficManageService.this.mTrafficManagers[i].mSimUser;
            return (!LeisureTrafficHelper.isLeisureTime(simUserInfo) || !simUserInfo.isLeisureDataUsageEffective()) ? simUserInfo.isLeisureDataUsageEffective() ? TrafficManageService.this.mTrafficManagers[i].getNormalTodayDataUsageNoLeisureUsed() : TrafficManageService.this.mTrafficManagers[i].getNormalTodayDataUsageUsed() : TrafficManageService.this.mTrafficManagers[i].getTodayLeisureDataUsage();
        }

        public ITrafficCornBinder getTrafficCornBinder(int i) {
            return TrafficManageService.this.mTrafficCornBinders[i];
        }

        public boolean isDataUsageIgnore(String str, int i) {
            DataUsageIgnoreAppListConfig dataUsageIgnoreAppListConfig = TrafficManageService.this.mTrafficManagers[i].mIgnoreAppListConfig;
            if (dataUsageIgnoreAppListConfig != null) {
                return dataUsageIgnoreAppListConfig.isDataUsageIgnore(str);
            }
            return false;
        }

        public boolean isNeededPurchasePkg(int i) {
            long currentMonthTotalPackage = getCurrentMonthTotalPackage(i);
            long correctedNormalMonthDataUsageUsed = getCorrectedNormalMonthDataUsageUsed(i);
            int dayOfMonth = DateUtil.getDayOfMonth();
            double d2 = (double) (currentMonthTotalPackage - correctedNormalMonthDataUsageUsed);
            boolean z = ((((double) correctedNormalMonthDataUsageUsed) * 1.0d) / ((double) dayOfMonth)) * ((double) (DateUtil.getActualMaxDayOfMonth() - dayOfMonth)) > d2;
            SimUserInfo simUserInfo = TrafficManageService.this.mTrafficManagers[i].mSimUser;
            return simUserInfo.isTotalDataUsageSetted() && simUserInfo.hasImsi() && simUserInfo.isNATrafficPurchaseAvailable() && (!simUserInfo.isNotLimitCardEnable() && ((d2 > Math.min(3.145728E8d, ((double) currentMonthTotalPackage) * 0.2d) ? 1 : (d2 == Math.min(3.145728E8d, ((double) currentMonthTotalPackage) * 0.2d) ? 0 : -1)) < 0) && z && dayOfMonth > 3);
        }

        public void manualCorrectLeisureDataUsage(long j, int i) {
            if (TrafficManageService.this.mTrafficManagers[i] != null) {
                TrafficManageService.this.mTrafficManagers[i].saveLatestCorrectedLeisureDataUsage(j);
                TrafficManageService.this.mTrafficManagers[i].mSimUser.saveLeisureDataUsageOverLimitWarningTime(0);
            }
        }

        public void manualCorrectNormalDataUsage(long j, int i) {
            if (TrafficManageService.this.mTrafficManagers[i] != null) {
                TrafficManageService.this.mTrafficManagers[i].saveLatestCorrectedNormalDataUsage(j);
                TrafficManageService.this.mTrafficManagers[i].mSimUser.saveDataUsageOverLimitStopNetworkWarningTime(0);
                TrafficManageService.this.mTrafficManagers[i].mSimUser.saveDataUsageOverLimitStopNetworkTime(0);
            }
        }

        public void reloadIgnoreAppList(int i) {
            TrafficManageService.this.mTrafficManagers[i].initDataUsageIgnoreAppList();
            DataUsageIgnoreAppListConfig dataUsageIgnoreAppListConfig = TrafficManageService.this.mTrafficManagers[i].mIgnoreAppListConfig;
            if (dataUsageIgnoreAppListConfig != null) {
                dataUsageIgnoreAppListConfig.saveNow();
            }
        }

        public void setDataUsageIgnore(String str, boolean z, int i) {
            DataUsageIgnoreAppListConfig dataUsageIgnoreAppListConfig = TrafficManageService.this.mTrafficManagers[i].mIgnoreAppListConfig;
            if (dataUsageIgnoreAppListConfig != null) {
                dataUsageIgnoreAppListConfig.setDataUsageIgnore(str, z);
            }
        }

        public boolean startCorrection(boolean z, int i, boolean z2, int i2) {
            if (TrafficManageService.this.mTrafficManagers[i] != null) {
                return TrafficManageService.this.mTrafficManagers[i].startCorrection(z, z2, i2);
            }
            return false;
        }

        public boolean startCorrectionDiagnostic(int i) {
            if (TrafficManageService.this.mTrafficManagers[i] != null) {
                return TrafficManageService.this.mTrafficManagers[i].startCorrectionDiagnostic();
            }
            return false;
        }

        public void toggleDataUsageAutoCorrection(boolean z, int i) {
            if (TrafficManageService.this.mTrafficManagers[i] != null) {
                TrafficManageService.this.mTrafficManagers[i].mSimUser.toggleDataUsageAutoCorrection(z);
                if (z) {
                    TrafficManageService.this.initDataUsageAutoCorrection();
                } else {
                    TrafficManageService.this.cancelDataUsageAutoCorrection();
                }
            }
        }

        public void toggleDataUsageOverLimitStopNetwork(boolean z, int i) {
            if (TrafficManageService.this.mTrafficManagers[i] != null) {
                TrafficManageService.this.mTrafficManagers[i].mSimUser.toggleDataUsageOverLimitStopNetwork(z);
                TrafficManageService.this.mTrafficManagers[i].initTrafficStatusMonitorVariable();
                TrafficManageService.this.mTrafficManagers[i].clearAllLimitTime();
            }
        }

        public void toggleLeisureDataUsageOverLimitWarning(boolean z, int i) {
            if (TrafficManageService.this.mTrafficManagers[i] != null) {
                TrafficManageService.this.mTrafficManagers[i].mSimUser.toggleLeisureDataUsageOverLimitWarning(z);
                TrafficManageService.this.mTrafficManagers[i].initTrafficStatusMonitorVariable();
                TrafficManageService.this.mTrafficManagers[i].clearAllLimitTime();
            }
        }

        public void updateGlobleDataUsage(int i) {
            if (TrafficManageService.this.mTrafficManagers[i] != null) {
                TrafficManageService.this.mTrafficManagers[i].updateTrafficCorrectionTotalLimit();
            }
            TrafficManageService.this.broadCastDataUsageUpdated();
        }

        public boolean updateTrafficCorrectonEngine(int i, String str, String str2, String str3) {
            return TrafficManageService.this.mTrafficManagers[i].updateTrafficCorrectionEngine(str, str2, str3);
        }

        public void updateTrafficStatusMonitor(int i) {
            Message obtainMessage = TrafficManageService.this.mBackgroundHandler.obtainMessage(1);
            obtainMessage.arg1 = i;
            TrafficManageService.this.mBackgroundHandler.sendMessage(obtainMessage);
        }
    }

    public TrafficManageService() {
        this.mHandlerThread.start();
        this.mBackgroundHandler = new Handler(this.mHandlerThread.getLooper(), this.mBackgroundHandlerCallback);
        this.mAppMonitor = new AppMonitor[2];
        this.mTrafficManagers = new TrafficSimManager[2];
        this.mTrafficCornBinders = new TrafficCornBinder[2];
        this.mTrafficCornBinders[0] = new TrafficCornBinder();
        if (DeviceUtil.IS_DUAL_CARD) {
            this.mTrafficCornBinders[1] = new TrafficCornBinder();
        }
    }

    /* access modifiers changed from: private */
    public void cancelNotificationWhenSimChanged() {
        if (!this.mSimCardHelper.isSimInserted()) {
            NotificationUtil.cancelNormalTotalPackageNotSetted(getApplicationContext());
            NotificationUtil.cancelSimLocationErrorNotify(getApplicationContext());
            NotificationUtil.cancelTcSmsReceivedNotify(getApplicationContext());
            NotificationUtil.cancelTcSmsTimeOutOrFailureNotify(getApplicationContext());
            NotificationUtil.cancelAllLowPriorityNotify(getApplicationContext());
            cancelNotificationWhenSlotChanged();
        }
    }

    private void cancelNotificationWhenSlotChanged() {
        NotificationUtil.cancelDataUsageOverLimit(getApplicationContext());
        NotificationUtil.cancelNormalDataUsageWarning(getApplicationContext());
        NotificationUtil.cancelDailyLimitWarning(getApplicationContext());
        NotificationUtil.cancelRoamingDailyLimitWarning(getApplicationContext());
    }

    /* access modifiers changed from: private */
    public void cancelWifiToMobile() {
        if (this.wifiToMobileShowing) {
            this.mBackgroundHandler.removeMessages(98);
            this.mBackgroundHandler.sendEmptyMessageDelayed(98, 500);
        }
    }

    /* access modifiers changed from: private */
    public void checkAllTrafficCorrectionEngineUpdate() {
        if (f.c(getApplicationContext()) == f.a.WifiConnected) {
            a.a(new Runnable() {
                public void run() {
                    TrafficManageService.this.mTrafficManagers[0].checkTrafficCorrectionEngineUpdate();
                    if (DeviceUtil.IS_DUAL_CARD) {
                        TrafficManageService.this.mTrafficManagers[1].checkTrafficCorrectionEngineUpdate();
                    }
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void checkAutoCorrectionConfig() {
        Log.i(TAG, "checkAutoCorrectionConfig");
        this.mTrafficManagers[0].updateAutoCorrectionConfig();
        if (DeviceUtil.IS_DUAL_CARD) {
            this.mTrafficManagers[1].updateAutoCorrectionConfig();
        }
    }

    /* access modifiers changed from: private */
    public void checkCachedTcSmsReport() {
        this.mTrafficManagers[0].reportSms();
        if (DeviceUtil.IS_DUAL_CARD) {
            this.mTrafficManagers[1].reportSms();
        }
    }

    /* access modifiers changed from: private */
    public void checkMiMobileConfig() {
        this.mTrafficManagers[0].checkMiMobileOperatorConfig();
        if (DeviceUtil.IS_DUAL_CARD) {
            this.mTrafficManagers[1].checkMiMobileOperatorConfig();
        }
    }

    /* access modifiers changed from: private */
    public boolean checkSmsShouldAutoCorrection(int i, long j) {
        SimUserInfo simUserInfo = this.mTrafficManagers[i].mSimUser;
        Log.i(TAG, "check sim auto correction: " + simUserInfo.toString());
        return checkTimeEffective(simUserInfo.getDataUsageAutoCorrectedTime(), j) && checkTimeEffective(simUserInfo.getDataUsageCorrectedTime(), j) && simUserInfo.isCorrectionEffective() && simUserInfo.isDataUsageAutoCorrectionEffective() && checkTimeEffective(simUserInfo.getDataRoamingStopUpdateTime(), j) && !WebCorrectionManager.getInstance(this).isCmccWebCorrectSupported(this.mTrafficManagers[i].mSimUser.getImsi());
    }

    private boolean checkTimeEffective(long j, long j2) {
        Log.i(TAG, String.format("checkTimeEffective，startTime：%s, endTime : %s", new Object[]{Long.valueOf(j), Long.valueOf(j2)}));
        return j < j2 || DateUtil.isLargerOffsetDay(j, j2, 1);
    }

    private boolean checkWebShouldCorrection(int i, long j) {
        SimUserInfo simUserInfo = this.mTrafficManagers[i].mSimUser;
        boolean z = checkTimeEffective(simUserInfo.getShouldWebCorrection(), j) && simUserInfo.isCorrectionEffective() && simUserInfo.isDataUsageAutoCorrectionEffective() && checkTimeEffective(simUserInfo.getDataUsageCorrectedTime(), j);
        simUserInfo.setShouldWebCorrection(DateUtil.getTodayTimeMillis() + 86400000);
        return z;
    }

    private PendingIntent createDataUsageAutoCorrectionIntent() {
        Intent intent = new Intent();
        intent.setAction(Constants.App.ACTION_DATA_USAGE_AUTO_CORRECTION);
        return PendingIntent.getBroadcast(this, 0, intent, 0);
    }

    private PendingIntent createRefreshDataUsageDailyIntent() {
        Intent intent = new Intent();
        intent.setAction(Constants.App.ACTION_REFRESH_DATA_USAGE_DAILY);
        return PendingIntent.getBroadcast(this, 0, intent, 0);
    }

    /* access modifiers changed from: private */
    public void handleAutoCorrectionMsg(int i, int i2) {
        SimUserInfo simUserInfo = this.mTrafficManagers[i].mSimUser;
        if (i2 != 0) {
            if (i2 != 1) {
                if (i2 != 2) {
                    if (i2 != 3) {
                        return;
                    }
                }
            } else if (TelephonyUtil.isSupportSmsCorrection(simUserInfo.getOperator()) || !f.l(getApplicationContext())) {
                return;
            }
            startAutoCorrectionChecked(i, 1);
            return;
        }
        startAutoCorrectionChecked(i, 7);
    }

    private void initFloatNotificationEnable() {
        if (!this.mCommonConfig.isFloatNotificationEnabled()) {
            b.b.o.a.a.a(this, getPackageName(), true);
            this.mCommonConfig.setFloatNotificationEnabled(true);
        }
    }

    /* access modifiers changed from: private */
    public void initInternationalRoaming() {
        TrafficSimManager[] trafficSimManagerArr = this.mTrafficManagers;
        int i = this.mActiveSlotNum;
        if (trafficSimManagerArr[i] != null) {
            SimUserInfo simUserInfo = trafficSimManagerArr[i].mSimUser;
            if (simUserInfo.isSimInserted() && simUserInfo.hasImsi()) {
                boolean dataRoamingEnabled = TelephonyUtil.getDataRoamingEnabled(getApplicationContext());
                boolean isNetworkRoaming = TelephonyUtil.isNetworkRoaming(getApplicationContext(), this.mActiveSlotNum);
                if (!dataRoamingEnabled && isNetworkRoaming) {
                    Log.i(TAG, "mina roaming active slot num :" + this.mActiveSlotNum);
                    NotificationUtil.sendOpenDataRoamingNotify(getApplicationContext());
                }
            }
        }
    }

    private void initLockScreenMonitor() {
        this.mLockScreenManager = new LockScreenManager(this, this.mTrafficManagers);
    }

    private void initMobileDataPolicyObserver() {
        this.mMobileDataPolicy = Settings.Secure.getInt(getContentResolver(), Constants.System.MOBILE_POLICY, 1);
    }

    private void initMonthReport() {
        this.mMonthReportTrafficManager = new DataUsageReportManager(this);
        this.mBackgroundHandler.sendEmptyMessageDelayed(22, 300000);
    }

    private void initNetworkBackgroundRestrict() {
        a.a(new Runnable() {
            public void run() {
                try {
                    BackgroundPolicyService.getInstance(TrafficManageService.this.getApplicationContext()).setRestrictBackground(false);
                } catch (Exception unused) {
                }
            }
        });
    }

    private void initNetworkStatsConfig() {
        Settings.Global.putLong(getContentResolver(), "netstats_uid_bucket_duration", 3600000);
        Settings.Global.putLong(getContentResolver(), "netstats_uid_tag_bucket_duration", 3600000);
    }

    private void initRefreshDataUsageDaily() {
        ((AlarmManager) getSystemService("alarm")).setRepeating(1, DateUtil.getTodayTimeMillis(), 86400000, createRefreshDataUsageDailyIntent());
        Log.i(TAG, "mina refresh data usage setted");
    }

    /* access modifiers changed from: private */
    public void initSim() {
        Log.i(TAG, "initSim");
        boolean updateSimState = this.mSimCardHelper.updateSimState();
        if (DeviceUtil.IS_DUAL_CARD) {
            updateActiveSlotNum();
            this.mTrafficManagers[1] = TrafficSimManager.getInstance(this, this.mSimCardHelper.getSim2Imsi());
            this.mTrafficCornBinders[1].setTrafficCorrection(this.mTrafficManagers[1].mTrafficCorrection);
        }
        this.mTrafficManagers[0] = TrafficSimManager.getInstance(this, this.mSimCardHelper.getSim1Imsi());
        this.mTrafficCornBinders[0].setTrafficCorrection(this.mTrafficManagers[0].mTrafficCorrection);
        checkMiMobileConfig();
        checkAutoCorrectionConfig();
        initDataUsageAutoCorrection();
        if (!updateSimState) {
            retryInitSim();
        }
    }

    private void initTrackAnalyticsManager() {
        this.mAnalyticsManager = new TrackAnalyticsManager(getApplicationContext(), this.mTrafficManagers);
    }

    /* access modifiers changed from: private */
    public void postInitSimMsgDelayed() {
        Log.i(TAG, "postInitSimMsgDelayed");
        if (this.mHandler.hasMessages(80)) {
            this.mHandler.removeMessages(80);
        }
        this.mHandler.sendEmptyMessageDelayed(80, 1000);
    }

    /* access modifiers changed from: private */
    public void postTrackUserDataDaily() {
        if (this.mBackgroundHandler.hasMessages(48)) {
            this.mBackgroundHandler.removeMessages(48);
        }
        this.mBackgroundHandler.sendEmptyMessageDelayed(48, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
    }

    /* access modifiers changed from: private */
    public void postUpdateTrafficCorrectionEngine() {
        if (this.mBackgroundHandler.hasMessages(49)) {
            this.mBackgroundHandler.removeMessages(49);
        }
        this.mBackgroundHandler.sendEmptyMessageDelayed(49, 1800000);
    }

    /* access modifiers changed from: private */
    public void postWifiToMobile() {
        if (!this.mBackgroundHandler.hasMessages(96) && !this.mBackgroundHandler.hasMessages(97)) {
            Log.i(TAG, "wifi2mobile: postWifiToMobile");
            this.mBackgroundHandler.sendEmptyMessageDelayed(96, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
        }
    }

    private void registerAutoCorrectionReceiver() {
        registerReceiver(this.mAutoCorrectionReceiver, new IntentFilter(Constants.App.ACTION_DATA_USAGE_AUTO_CORRECTION));
    }

    private void registerCloudDataObserver() {
        getContentResolver().registerContentObserver(MiuiSettingsCompat$SettingsCloudData.a(), true, this.mMiSimCloudDataObserver);
    }

    private void registerDeviceProvisionedObserver() {
        getContentResolver().registerContentObserver(Settings.Secure.getUriFor(Constants.System.DEVICE_PROVISIONED), true, this.mDeviceProvisionedObserver);
    }

    private void registerExtraNetworkReceiver() {
        Log.i(TAG, "mExtraNetworkReceiver registerExtraNetworkReceiver ");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.System.ACTION_NETWORK_BLOCKED);
        intentFilter.addAction(Constants.System.ACTION_NETWORK_CONNECTED);
        registerReceiver(this.mExtraNetworkReceiver, intentFilter);
    }

    private void registerMobileDataEnableObserver() {
        c.a a2 = c.a.a("android.app.MobileDataUtils");
        a2.b("getInstance", (Class<?>[]) null, new Object[0]);
        a2.e();
        a2.a("registerContentObserver", new Class[]{Context.class, ContentObserver.class}, getApplicationContext(), this.mMobileDataEnableObserver);
    }

    private void registerMobileDataPolicyObserver() {
        getContentResolver().registerContentObserver(Settings.Secure.getUriFor(Constants.System.MOBILE_POLICY), false, this.mMobileDataPolicyObserver);
    }

    private void registerNetworkConnectivityReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(this.mNetworkConnectivityReceiver, intentFilter);
    }

    private void registerPackageReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.System.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Constants.System.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        g.a((Context) this, this.mPackageReceiver, B.d(), intentFilter);
    }

    private void registerPhoneStateListener() {
        ((TelephonyManager) getSystemService("phone")).listen(this.mPhoneStateListener, 256);
    }

    private void registerPurchaseSuccessReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.App.ACTION_PURCHASE_SUCCESS);
        registerReceiver(this.mPurchaseSuccessReceiver, intentFilter);
    }

    private void registerRefreshDataUsageDailyReceiver() {
        registerReceiver(this.mRefreshDataUsageDailyReceiver, new IntentFilter(Constants.App.ACTION_REFRESH_DATA_USAGE_DAILY));
    }

    private void registerScNetworkStatusReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action_update_sc_network_allow");
        registerReceiver(this.mScNetworkStatusReceiver, intentFilter);
    }

    private void registerScreenReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.System.ACTION_SCREEN_ON);
        intentFilter.addAction(Constants.System.ACTION_SCREEN_OFF);
        intentFilter.addAction(Constants.System.ACTION_USER_PRESENT);
        registerReceiver(this.mLockScreenReceiver, intentFilter, (String) null, this.mBackgroundHandler);
    }

    private void registerSimDataSlotStateReceiver() {
        registerReceiver(this.mSimStateDataSlotReceiver, new IntentFilter(Constants.System.ACTION_DEFAULT_DATA_SLOT_CHANGED));
    }

    private void registerSimStateReceiver() {
        registerReceiver(this.mSimStateReceiver, new IntentFilter(Constants.System.ACTION_SIM_STATE_CHANGED));
        DualSimInfoManager.registerChangeListener(this, this.mSimInfoChangeListener);
    }

    private void registerSmsReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.System.ACTION_SMS_RECEIVED);
        intentFilter.setPriority(Integer.MAX_VALUE);
        intentFilter.addCategory(Constants.System.CATEGORY_DEFALUT);
        registerReceiver(this.mSmsReceiver, intentFilter, Constants.System.PERMISSION_BROADCAST_SMS, (Handler) null);
    }

    private void registerTrafficStatsReceiver() {
        registerReceiver(this.mTrafficStatsReceiver, new IntentFilter(Constants.System.ACTION_NETWORK_STATS_UPDATED), Constants.System.PERMISSION_READ_NETWORK_USAGE_HISTORY, this.mBackgroundHandler);
    }

    private void registerUserSwitchReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_FOREGROUND");
        intentFilter.addAction("android.intent.action.USER_BACKGROUND");
        registerReceiver(this.mUserSwitchReceiver, intentFilter);
    }

    private void registerWifiApReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
        registerReceiver(this.mWifiApStateReceiver, intentFilter);
    }

    private void registerWifiNetworkStatusReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        registerReceiver(this.mWifiNetworkStatusReceiver, intentFilter);
    }

    private void registerXmanReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.miui.securitycenter.intent.action.SHARED");
        intentFilter.addAction("com.miui.securitycenter.intent.action.XMAN.SECURITY_SHARE_SETTINGS_SHOW");
        registerReceiver(this.mXmanShareReceiver, intentFilter);
    }

    private void registerZmanReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.miui.zman.intent.action.SHARED");
        intentFilter.addAction("com.miui.zman.intent.action.VIEW_SHOW");
        intentFilter.addAction("com.miui.zman.intent.action.VIEW_CHANGE");
        registerReceiver(this.mZmanShareReceiver, intentFilter);
    }

    private void retryInitSim() {
        Log.i(TAG, String.format("retryInitSim, retryTime:%d", new Object[]{Integer.valueOf(this.mRetryTime)}));
        if (!this.mHandler.hasMessages(80)) {
            int i = this.mRetryTime;
            this.mRetryTime = i + 1;
            if (i < 10) {
                this.mHandler.sendEmptyMessageDelayed(80, 10000);
            }
        }
    }

    /* access modifiers changed from: private */
    public void startAutoCorrection() {
        Log.i(TAG, "startAutoCorrection");
        long todayTimeMillis = DateUtil.getTodayTimeMillis();
        boolean checkWebShouldCorrection = checkWebShouldCorrection(0, todayTimeMillis);
        if (checkWebShouldCorrection) {
            Message obtainMessage = this.mHandler.obtainMessage(19);
            obtainMessage.arg1 = 0;
            obtainMessage.arg2 = 1;
            this.mHandler.sendMessageDelayed(obtainMessage, 60000);
        }
        if (DeviceUtil.IS_DUAL_CARD && checkWebShouldCorrection(1, todayTimeMillis)) {
            long j = checkWebShouldCorrection ? 550000 : 0;
            Message obtainMessage2 = this.mHandler.obtainMessage(19);
            obtainMessage2.arg1 = 1;
            obtainMessage2.arg2 = 1;
            this.mHandler.sendMessageDelayed(obtainMessage2, j);
        }
    }

    private void startAutoCorrectionChecked(int i, int i2) {
        this.mTrafficManagers[i].mSimUser.saveDataUsageAutoCorrectedTime(System.currentTimeMillis());
        this.mTrafficManagers[i].startCorrection(true, false, i2);
    }

    /* access modifiers changed from: private */
    public void startCmccAutoCorrection() {
        Log.i(TAG, "startCmccAutoCorrection");
        if (checkWebShouldCorrection(this.mActiveSlotNum, DateUtil.getTodayTimeMillis()) && WebCorrectionManager.getInstance(this).isCmccWebCorrectSupported(this.mTrafficManagers[this.mActiveSlotNum].mSimUser.getImsi())) {
            Message obtainMessage = this.mHandler.obtainMessage(19);
            obtainMessage.arg1 = this.mActiveSlotNum;
            obtainMessage.arg2 = 3;
            this.mHandler.sendMessageDelayed(obtainMessage, 60000);
        }
    }

    private void unRegisterAutoCorrectionReceiver() {
        unregisterReceiver(this.mAutoCorrectionReceiver);
    }

    private void unRegisterDeviceProvisionedObserver() {
        getContentResolver().unregisterContentObserver(this.mDeviceProvisionedObserver);
    }

    private void unRegisterExtraNetworkReceiver() {
        unregisterReceiver(this.mExtraNetworkReceiver);
    }

    private void unRegisterMobileDataEnableObserver() {
        getContentResolver().unregisterContentObserver(this.mMobileDataEnableObserver);
    }

    private void unRegisterMobileDataPolicyObserver() {
        getContentResolver().unregisterContentObserver(this.mMobileDataPolicyObserver);
    }

    private void unRegisterNetworkConnectivityReceiver() {
        unregisterReceiver(this.mNetworkConnectivityReceiver);
    }

    private void unRegisterPackageReceiver() {
        unregisterReceiver(this.mPackageReceiver);
    }

    private void unRegisterPhoneStateListener() {
        ((TelephonyManager) getSystemService("phone")).listen(this.mPhoneStateListener, 0);
    }

    private void unRegisterPurchaseSuccessReceiver() {
        unregisterReceiver(this.mPurchaseSuccessReceiver);
    }

    private void unRegisterRefreshDataUsageDailyReceiver() {
        unregisterReceiver(this.mRefreshDataUsageDailyReceiver);
    }

    private void unRegisterScNetworkStatusReceiver() {
        unregisterReceiver(this.mScNetworkStatusReceiver);
    }

    private void unRegisterScreenReceiver() {
        unregisterReceiver(this.mLockScreenReceiver);
    }

    private void unRegisterSimDataSlotStateReceiver() {
        unregisterReceiver(this.mSimStateDataSlotReceiver);
    }

    private void unRegisterSimStateReceiver() {
        unregisterReceiver(this.mSimStateReceiver);
        DualSimInfoManager.unRegisterChangeListener(this, this.mSimInfoChangeListener);
    }

    private void unRegisterSmsReceiver() {
        unregisterReceiver(this.mSmsReceiver);
    }

    private void unRegisterTrafficStatsReceiver() {
        unregisterReceiver(this.mTrafficStatsReceiver);
    }

    private void unRegisterUserSwitchReceiver() {
        unregisterReceiver(this.mUserSwitchReceiver);
    }

    private void unRegisterWifiApReceiver() {
        unregisterReceiver(this.mWifiApStateReceiver);
    }

    private void unRegisterWifiNetworkStatusReceiver() {
        unregisterReceiver(this.mWifiNetworkStatusReceiver);
    }

    /* access modifiers changed from: private */
    public void updateActiveSlotNum() {
        int currentMobileSlotNum = this.mSimCardHelper.getCurrentMobileSlotNum();
        if (currentMobileSlotNum != this.mActiveSlotNum) {
            this.mActiveSlotNum = currentMobileSlotNum;
            cancelNotificationWhenSlotChanged();
        }
        final TrafficSimManager trafficSimManager = this.mTrafficManagers[this.mActiveSlotNum];
        if (trafficSimManager != null) {
            a.a(new Runnable() {
                public void run() {
                    MiServiceFrameworkHelper.updateSim(TrafficManageService.this.getApplicationContext(), trafficSimManager.mSimUser.getImsi());
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void updateNormalTotalPackageSetted(Intent intent) {
        if (this.mIsDeviceProvisioned && Constants.System.ACTION_USER_PRESENT.equals(intent.getAction())) {
            boolean z = SystemClock.elapsedRealtime() > 3600000;
            TrafficSimManager[] trafficSimManagerArr = this.mTrafficManagers;
            int i = this.mActiveSlotNum;
            if (trafficSimManagerArr[i] != null && z) {
                trafficSimManagerArr[i].checkNormalTotalPackageSetted();
                this.mIsDeviceProvisioned = false;
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateRoamingStateChanged() {
        if (f.e(getApplicationContext())) {
            initInternationalRoaming();
        }
    }

    /* access modifiers changed from: package-private */
    public void broadCastDataUsageUpdated() {
        TrafficUpdateUtil.broadCastTrafficUpdated(getApplicationContext());
    }

    /* access modifiers changed from: package-private */
    public void cancelDataUsageAutoCorrection() {
        if (this.mTrafficManagers[0].mSimUser.isDataUsageAutoCorrectionEffective()) {
            return;
        }
        if (!DeviceUtil.IS_DUAL_CARD || !this.mTrafficManagers[1].mSimUser.isDataUsageAutoCorrectionEffective()) {
            ((AlarmManager) getSystemService("alarm")).cancel(createDataUsageAutoCorrectionIntent());
            Log.i(TAG, "mina auto correction canceled");
        }
    }

    public int getMobileDataPolicy() {
        return this.mMobileDataPolicy;
    }

    /* access modifiers changed from: package-private */
    public void initDataUsageAutoCorrection() {
        int i = 0;
        if (this.mTrafficManagers[0].mSimUser.isDataUsageAutoCorrectionEffective() || (DeviceUtil.IS_DUAL_CARD && this.mTrafficManagers[1].mSimUser.isDataUsageAutoCorrectionEffective())) {
            long todayTimeMillis = DateUtil.getTodayTimeMillis();
            boolean checkSmsShouldAutoCorrection = checkSmsShouldAutoCorrection(0, todayTimeMillis);
            if (DeviceUtil.IS_DUAL_CARD) {
                checkSmsShouldAutoCorrection |= checkSmsShouldAutoCorrection(1, todayTimeMillis);
            }
            if (!checkSmsShouldAutoCorrection) {
                i = 24;
            }
            ((AlarmManager) getSystemService("alarm")).setRepeating(1, todayTimeMillis + (((long) (i + 8)) * 3600000) + ((long) (Math.random() * 4.32E7d)), 86400000, createDataUsageAutoCorrectionIntent());
            Log.i(TAG, "mina auto correction setted");
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isDeviceProvisioned() {
        return Settings.Secure.getInt(getContentResolver(), Constants.System.DEVICE_PROVISIONED, 0) != 0;
    }

    public IBinder onBind(Intent intent) {
        return this.mTrafficManageBinder.asBinder();
    }

    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "mina onCreate");
        PreSetGroup.initGroupMap(this);
        this.mCurrentUserIndex = B.b(B.c());
        this.mAppMonitor[0] = new AppMonitor(getApplicationContext());
        this.mAppMonitor[1] = new AppMonitor(getApplicationContext());
        this.mAppMonitor[this.mCurrentUserIndex].initData(B.c());
        this.mCommonConfig = CommonConfig.getInstance(this);
        this.mSimCardHelper = SimCardHelper.getInstance(this);
        this.mTrafficManageBinder = new TrafficManageBinder();
        if (!DeviceUtil.IS_INTERNATIONAL_BUILD) {
            this.mNetworkCheckStateManager = new NetworkCheckStateManager(this);
        } else {
            this.mNetworkCheckStateManager = null;
        }
        this.mPurchaseSmsManager = new PurchaseSmsManager(this);
        this.mTetherStatsManager = new TetherStatsManager(this);
        this.mTetherStatsManager.initTetheringStatus();
        initSim();
        initNetworkStatsConfig();
        initMobileDataPolicyObserver();
        initLockScreenMonitor();
        initMonthReport();
        initFloatNotificationEnable();
        initTrackAnalyticsManager();
        initRefreshDataUsageDaily();
        initNetworkBackgroundRestrict();
        registerXmanReceiver();
        registerZmanReceiver();
        registerTrafficStatsReceiver();
        registerSimStateReceiver();
        registerSimDataSlotStateReceiver();
        registerAutoCorrectionReceiver();
        registerDeviceProvisionedObserver();
        registerNetworkConnectivityReceiver();
        registerMobileDataEnableObserver();
        registerMobileDataPolicyObserver();
        registerScreenReceiver();
        registerRefreshDataUsageDailyReceiver();
        registerExtraNetworkReceiver();
        registerPackageReceiver();
        registerSmsReceiver();
        registerUserSwitchReceiver();
        registerScNetworkStatusReceiver();
        registerPurchaseSuccessReceiver();
        registerPhoneStateListener();
        registerCloudDataObserver();
        registerWifiApReceiver();
        registerWifiNetworkStatusReceiver();
    }

    public void onDestroy() {
        super.onDestroy();
        unRegisterXmanReceiver();
        unRegisterZmanReceiver();
        unRegisterTrafficStatsReceiver();
        unRegisterSimStateReceiver();
        unRegisterSimDataSlotStateReceiver();
        unRegisterAutoCorrectionReceiver();
        unRegisterDeviceProvisionedObserver();
        unRegisterNetworkConnectivityReceiver();
        unRegisterMobileDataEnableObserver();
        unRegisterMobileDataPolicyObserver();
        unRegisterScreenReceiver();
        unRegisterRefreshDataUsageDailyReceiver();
        unRegisterExtraNetworkReceiver();
        unRegisterPackageReceiver();
        unRegisterSmsReceiver();
        unRegisterUserSwitchReceiver();
        unRegisterScNetworkStatusReceiver();
        unRegisterPurchaseSuccessReceiver();
        unRegisterPhoneStateListener();
        unRegisterWifiApReceiver();
        unRegisterWifiNetworkStatusReceiver();
        NetworkCheckStateManager networkCheckStateManager = this.mNetworkCheckStateManager;
        if (networkCheckStateManager != null) {
            networkCheckStateManager.onDestroy();
        }
    }

    /* access modifiers changed from: package-private */
    public void unRegisterXmanReceiver() {
        unregisterReceiver(this.mXmanShareReceiver);
    }

    /* access modifiers changed from: package-private */
    public void unRegisterZmanReceiver() {
        unregisterReceiver(this.mZmanShareReceiver);
    }
}
