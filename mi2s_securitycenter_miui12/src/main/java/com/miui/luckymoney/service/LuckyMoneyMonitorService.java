package com.miui.luckymoney.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.service.notification.StatusBarNotification;
import android.telephony.PhoneStateListener;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.j.B;
import b.b.c.j.g;
import b.b.c.j.i;
import b.b.o.g.c;
import com.miui.gamebooster.service.ISecurityCenterNotificationListener;
import com.miui.gamebooster.service.NotificationListener;
import com.miui.gamebooster.service.NotificationListenerCallback;
import com.miui.luckymoney.config.AppConstants;
import com.miui.luckymoney.config.CommonConfig;
import com.miui.luckymoney.config.Constants;
import com.miui.luckymoney.controller.Pipeline;
import com.miui.luckymoney.model.config.BaseConfiguration;
import com.miui.luckymoney.model.config.impl.BusinessConfiguration;
import com.miui.luckymoney.model.config.impl.DefaultConfiguration;
import com.miui.luckymoney.model.config.impl.MiTalkConfiguration;
import com.miui.luckymoney.model.config.impl.QQConfiguration;
import com.miui.luckymoney.model.message.Impl.BusinessMessage;
import com.miui.luckymoney.model.message.Impl.MiTalkMessage;
import com.miui.luckymoney.model.message.Impl.QQMessage;
import com.miui.luckymoney.model.message.Impl.WechatMessage;
import com.miui.luckymoney.service.QQGroupCollector;
import com.miui.luckymoney.ui.activity.RemoveLuckyMoneyActivity;
import com.miui.luckymoney.ui.view.DesktopFloatAssistantView;
import com.miui.luckymoney.ui.view.GeneralMessageViewCreator;
import com.miui.luckymoney.utils.DateUtil;
import com.miui.luckymoney.utils.NotificationUtil;
import com.miui.luckymoney.webapi.FloatResourceHelper;
import com.miui.luckymoney.webapi.LuckyAlarmResult;
import com.miui.networkassistant.config.Constants;

public class LuckyMoneyMonitorService extends Service {
    private static final String EXTRA_ANDROID_TEXT = "android.text";
    private static final String EXTRA_ANDROID_TITLE = "android.title";
    public static final int MSG_REMOVE_FLOAT_TIPS = 4;
    private static final int MSG_SENSOR_SHAKE = 1;
    private static final int MSG_UPDATE_CONFIG = 2;
    private static final int MSG_UPLOAD_SETTING_SWITCH_STATE = 3;
    /* access modifiers changed from: private */
    public static final String TAG = "LuckyMoneyMonitorService";
    /* access modifiers changed from: private */
    public DesktopFloatAssistantView mAssistantFloatView;
    private BaseConfiguration mBusinessConfig = null;
    /* access modifiers changed from: private */
    public Pipeline mBusinessPipeline = null;
    private CloudControlReceiver mCloudControlReceiver = new CloudControlReceiver();
    /* access modifiers changed from: private */
    public CommonConfig mCommonConfig;
    private BroadcastReceiver mConfigChangedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String stringExtra;
            if (Constants.ACTION_CONFIG_CHANGED_BROADCAST.equals(intent.getAction()) && (stringExtra = intent.getStringExtra(Constants.KEY_CONFIG_CHANGED_FLAG)) != null) {
                Message obtainMessage = LuckyMoneyMonitorService.this.mMainHandler.obtainMessage(2);
                obtainMessage.obj = stringExtra;
                LuckyMoneyMonitorService.this.mMainHandler.sendMessage(obtainMessage);
            }
        }
    };
    private BroadcastReceiver mConfigUpdateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.i(LuckyMoneyMonitorService.TAG, "mFloatTipsConfigUpdateReceiver.onReceive");
            String action = intent.getAction();
            if (Constants.ACTION_UPDATE_TIPS_CONFIG.equals(action)) {
                Log.d(LuckyMoneyMonitorService.TAG, "update tips config");
            } else if (Constants.ACTION_UPDATE_ALARM_CONFIG.equals(action)) {
                Log.d(LuckyMoneyMonitorService.TAG, "update alarm config");
                LuckyMoneyMonitorService.this.checkLuckyAlarmLocalConfig();
                return;
            } else if (Constants.ACTION_FLOAT_TIPS_ACTIVITY_END.endsWith(action)) {
                Log.d(LuckyMoneyMonitorService.TAG, "float tips activity end");
                LuckyMoneyMonitorService.this.mMainHandler.sendEmptyMessage(4);
            } else {
                new Handler(context.getMainLooper()).postDelayed(new Runnable() {
                    public void run() {
                        LuckyMoneyMonitorService.this.checkFloatTipsConfigUpdate();
                        LuckyMoneyMonitorService.this.checkLuckyAlarmLocalConfig();
                    }
                }, 10000);
                return;
            }
            LuckyMoneyMonitorService.this.checkFloatTipsConfigUpdate();
        }
    };
    /* access modifiers changed from: private */
    public Context mContext;
    private Object mFloatViewLock = new Object();
    private Handler.Callback mHandlerCallback = new Handler.Callback() {
        /* JADX WARNING: Code restructure failed: missing block: B:22:0x006e, code lost:
            if (com.miui.luckymoney.config.Constants.TYPE_LUCKY_OPEN.equals(r4) != false) goto L_0x0054;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean handleMessage(android.os.Message r4) {
            /*
                r3 = this;
                int r0 = r4.what
                r1 = 1
                if (r0 == r1) goto L_0x0071
                r2 = 2
                if (r0 == r2) goto L_0x002e
                r4 = 3
                if (r0 == r4) goto L_0x0024
                r4 = 4
                if (r0 == r4) goto L_0x0010
                goto L_0x0083
            L_0x0010:
                com.miui.luckymoney.service.LuckyMoneyMonitorService r4 = com.miui.luckymoney.service.LuckyMoneyMonitorService.this
                r4.removeAssistantFloatView()
                com.miui.luckymoney.service.LuckyMoneyMonitorService r4 = com.miui.luckymoney.service.LuckyMoneyMonitorService.this
                r4.addAssistantFloatView()
                java.lang.String r4 = com.miui.luckymoney.service.LuckyMoneyMonitorService.TAG
                java.lang.String r0 = "exce remove float view"
                android.util.Log.i(r4, r0)
                goto L_0x0083
            L_0x0024:
                com.miui.luckymoney.service.LuckyMoneyMonitorService r4 = com.miui.luckymoney.service.LuckyMoneyMonitorService.this
                android.content.Context r4 = r4.mContext
                com.miui.luckymoney.stats.MiStatUtil.trackSettingSwitchState(r4)
                goto L_0x0083
            L_0x002e:
                java.lang.Object r4 = r4.obj
                java.lang.String r4 = (java.lang.String) r4
                java.lang.String r0 = "show_float_window"
                boolean r0 = r0.equals(r4)
                if (r0 == 0) goto L_0x004c
                com.miui.luckymoney.service.LuckyMoneyMonitorService r4 = com.miui.luckymoney.service.LuckyMoneyMonitorService.this
                com.miui.luckymoney.config.CommonConfig r4 = r4.mCommonConfig
                boolean r4 = r4.isDesktopFloatWindowEnable()
                if (r4 == 0) goto L_0x0083
                com.miui.luckymoney.service.LuckyMoneyMonitorService r4 = com.miui.luckymoney.service.LuckyMoneyMonitorService.this
                r4.addAssistantFloatView()
                goto L_0x0083
            L_0x004c:
                java.lang.String r0 = "show_float_window_button"
                boolean r0 = r0.equals(r4)
                if (r0 == 0) goto L_0x005a
            L_0x0054:
                com.miui.luckymoney.service.LuckyMoneyMonitorService r4 = com.miui.luckymoney.service.LuckyMoneyMonitorService.this
                r4.processAssistantFloatView()
                goto L_0x0083
            L_0x005a:
                java.lang.String r0 = "remove_float_window"
                boolean r0 = r0.equals(r4)
                if (r0 == 0) goto L_0x0068
                com.miui.luckymoney.service.LuckyMoneyMonitorService r4 = com.miui.luckymoney.service.LuckyMoneyMonitorService.this
                r4.removeAssistantFloatView()
                goto L_0x0083
            L_0x0068:
                java.lang.String r0 = "lucky_open"
                boolean r4 = r0.equals(r4)
                if (r4 == 0) goto L_0x0083
                goto L_0x0054
            L_0x0071:
                java.lang.String r4 = com.miui.luckymoney.service.LuckyMoneyMonitorService.TAG
                java.lang.String r0 = "MSG_SENSOR_SHAKE"
                android.util.Log.i(r4, r0)
                com.miui.luckymoney.service.LuckyMoneyMonitorService r4 = com.miui.luckymoney.service.LuckyMoneyMonitorService.this
                android.content.Context r4 = r4.mContext
                com.miui.luckymoney.utils.PackageUtil.startStickerActivityWithVibrator(r4)
            L_0x0083:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.luckymoney.service.LuckyMoneyMonitorService.AnonymousClass11.handleMessage(android.os.Message):boolean");
        }
    };
    private BroadcastReceiver mLockScreenReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, Constants.System.ACTION_USER_PRESENT)) {
                if (LuckyMoneyMonitorService.this.mAssistantFloatView != null) {
                    LuckyMoneyMonitorService.this.mAssistantFloatView.removeCancleFloatView();
                }
            } else if (TextUtils.equals(action, Constants.System.ACTION_SCREEN_OFF)) {
                LuckyMoneyMonitorService.this.mMainHandler.sendMessage(LuckyMoneyMonitorService.this.mMainHandler.obtainMessage(3));
            }
        }
    };
    private LuckyAlarmReceiver mLuckyAlarmReceiver = new LuckyAlarmReceiver();
    public Handler mMainHandler = null;
    private BaseConfiguration mMessageConfig = null;
    private BaseConfiguration mMiTalkConfig = null;
    /* access modifiers changed from: private */
    public Pipeline mMitalkPipeline = null;
    /* access modifiers changed from: private */
    public ISecurityCenterNotificationListener mNoticationListenerBinder;
    /* access modifiers changed from: private */
    public NotificationListenerCallback mNotificationListenerCallback = new NotificationListenerCallback() {
        public void onNotificationPostedCallBack(StatusBarNotification statusBarNotification) {
            if (statusBarNotification.getUserId() == B.c() && statusBarNotification.getUserId() == B.j()) {
                LuckyMoneyMonitorService.this.onNotificationPosted(statusBarNotification);
            }
        }

        public void onNotificationRemovedCallBack(StatusBarNotification statusBarNotification) {
        }
    };
    private ServiceConnection mNotificationListenerConn = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ISecurityCenterNotificationListener unused = LuckyMoneyMonitorService.this.mNoticationListenerBinder = ISecurityCenterNotificationListener.Stub.a(iBinder);
            try {
                LuckyMoneyMonitorService.this.mNoticationListenerBinder.b(LuckyMoneyMonitorService.this.mNotificationListenerCallback);
            } catch (Exception e) {
                String access$700 = LuckyMoneyMonitorService.TAG;
                Log.e(access$700, "mNoticationListenerBinder:" + e);
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            ISecurityCenterNotificationListener unused = LuckyMoneyMonitorService.this.mNoticationListenerBinder = null;
        }
    };
    private BroadcastReceiver mPackageReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.i(LuckyMoneyMonitorService.TAG, "mina mPackageReceiver onReceive");
            Uri data = intent.getData();
            String schemeSpecificPart = data != null ? data.getSchemeSpecificPart() : null;
            if (!TextUtils.isEmpty(schemeSpecificPart) && AppConstants.Package.PACKAGE_NAME_HB.equals(schemeSpecificPart)) {
                Intent intent2 = new Intent(context, RemoveLuckyMoneyActivity.class);
                intent2.setFlags(268435456);
                g.b(context, intent2, B.b());
            }
        }
    };
    private PhoneRingMonitor mPhoneStateMonitor;
    private BaseConfiguration mQQConfig = null;
    /* access modifiers changed from: private */
    public Pipeline mQQPipeline = null;
    private BroadcastReceiver mRefreshFloatTipsDailyReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.i(LuckyMoneyMonitorService.TAG, "mina mRefreshFloatTipsDailyReceiver ");
            if (LuckyMoneyMonitorService.this.mCommonConfig.isDesktopFloatWindowEnable()) {
                if (!DateUtil.isTipsTimeEnable(context)) {
                    LuckyMoneyMonitorService.this.checkFloatTipsConfigUpdate();
                    return;
                }
                LuckyMoneyMonitorService.this.removeAssistantFloatView();
                LuckyMoneyMonitorService.this.addAssistantFloatView();
                Intent intent2 = new Intent();
                intent2.setAction(com.miui.luckymoney.config.Constants.ACTION_FLOAT_TIPS_ACTIVITY_END);
                ((AlarmManager) context.getSystemService("alarm")).set(1, LuckyMoneyMonitorService.this.mCommonConfig.getFloatTipsStopTime() + LuckyMoneyMonitorService.this.mCommonConfig.getFloatTipsDuration(), PendingIntent.getBroadcast(context, 0, intent2, 0));
            }
        }
    };
    /* access modifiers changed from: private */
    public Pipeline mWeixinPipeline = null;

    private class PhoneRingMonitor extends PhoneStateListener {
        private PhoneRingMonitor() {
        }

        public void onCallStateChanged(int i, String str) {
            Handler handler;
            super.onCallStateChanged(i, str);
            if (i == 1 && (handler = LuckyMoneyMonitorService.this.mMainHandler) != null) {
                handler.post(new Runnable() {
                    public void run() {
                        if (LuckyMoneyMonitorService.this.mWeixinPipeline != null) {
                            LuckyMoneyMonitorService.this.mWeixinPipeline.notifyPhoneArrived();
                        }
                        if (LuckyMoneyMonitorService.this.mQQPipeline != null) {
                            LuckyMoneyMonitorService.this.mQQPipeline.notifyPhoneArrived();
                        }
                        if (LuckyMoneyMonitorService.this.mMitalkPipeline != null) {
                            LuckyMoneyMonitorService.this.mMitalkPipeline.notifyPhoneArrived();
                        }
                        if (LuckyMoneyMonitorService.this.mBusinessPipeline != null) {
                            LuckyMoneyMonitorService.this.mBusinessPipeline.notifyPhoneArrived();
                        }
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    public void addAssistantFloatView() {
        synchronized (this.mFloatViewLock) {
            if (this.mAssistantFloatView == null) {
                this.mAssistantFloatView = new DesktopFloatAssistantView(this);
                this.mAssistantFloatView.createFloatView();
            }
        }
    }

    /* access modifiers changed from: private */
    public void checkFloatTipsConfigUpdate() {
        FloatResourceHelper.checkFloatTipsConfigLocalUpdate(this);
        initRefreshFloatTipsDaily();
    }

    /* access modifiers changed from: private */
    public void checkLuckyAlarmLocalConfig() {
        new LuckyAlarmResult(this.mCommonConfig.getLuckyAlarmConfig(), true);
    }

    private PendingIntent createRefreshFloatTipsDailyIntent() {
        Intent intent = new Intent();
        intent.setAction(com.miui.luckymoney.config.Constants.ACTION_REFRESH_FLOAT_TIPS_DAILY);
        return PendingIntent.getBroadcast(this, 0, intent, 0);
    }

    private long getTriggerAtMillisByIMEI() {
        long j;
        long floatTipsStartTime = this.mCommonConfig.getFloatTipsStartTime();
        long floatTipsStopTime = this.mCommonConfig.getFloatTipsStopTime();
        String c2 = i.c(this);
        if (TextUtils.isEmpty(c2)) {
            return floatTipsStartTime + ((long) (((double) (floatTipsStopTime - floatTipsStartTime)) * Math.random()));
        }
        switch (c2.charAt(c2.length() - 1)) {
            case '0':
            case '1':
                j = 0;
                break;
            case '2':
            case '3':
                j = (floatTipsStopTime - floatTipsStartTime) / 4;
                break;
            case '4':
            case '5':
                j = (floatTipsStopTime - floatTipsStartTime) / 2;
                break;
            case '6':
            case '7':
                j = ((floatTipsStopTime - floatTipsStartTime) / 4) * 3;
                break;
            case '8':
            case '9':
                j = floatTipsStopTime - floatTipsStartTime;
                break;
            default:
                j = (long) (((double) (floatTipsStopTime - floatTipsStartTime)) * Math.random());
                break;
        }
        return floatTipsStartTime + j;
    }

    private void initRefreshFloatTipsDaily() {
        if (!this.mCommonConfig.isDesktopFloatWindowEnable()) {
            Log.d(TAG, "initRefreshFloatTipsDaily:DesktopFloatWindow disable");
        } else if (!shouldPerparFloatTipsAlerm()) {
            Log.d(TAG, "initRefreshFloatTipsDaily:shouldPerparFloatTipsAlerm false");
        } else {
            long triggerAtMillisByIMEI = getTriggerAtMillisByIMEI();
            ((AlarmManager) getSystemService("alarm")).setExact(1, triggerAtMillisByIMEI, createRefreshFloatTipsDailyIntent());
            String str = TAG;
            Log.i(str, "mina refresh float tips setted:" + triggerAtMillisByIMEI);
        }
    }

    /* access modifiers changed from: private */
    public void processAssistantFloatView() {
        if (this.mCommonConfig.isDesktopFloatWindowEnable()) {
            addAssistantFloatView();
        } else {
            removeAssistantFloatView();
        }
    }

    private void processTypeUnknownNotification(StatusBarNotification statusBarNotification, final QQMessage qQMessage) {
        QQMessage qQMessage2;
        Notification notification = statusBarNotification.getNotification();
        if (notification != null && qQMessage.isHongbao() && qQMessage.isGroupMessage()) {
            String str = qQMessage.message;
            String str2 = qQMessage.from;
            String str3 = qQMessage.conversationName;
            PendingIntent pendingIntent = notification.contentIntent;
            if (!TextUtils.isEmpty(str3) && pendingIntent != null) {
                QQGroupCollector.QQGroupInfo findQQGroupByName = QQGroupCollector.findQQGroupByName(str3);
                notification.extras.putCharSequence(EXTRA_ANDROID_TEXT, str2 + ": " + str);
                notification.extras.putCharSequence(EXTRA_ANDROID_TITLE, str3);
                c.a a2 = c.a.a((Object) pendingIntent);
                a2.a("getIntent", (Class<?>[]) null, new Object[0]);
                Intent intent = (Intent) a2.d();
                if (intent != null) {
                    Intent intent2 = (Intent) intent.clone();
                    if (findQQGroupByName != null) {
                        intent2.putExtra(QQMessage.KEY_CONVERSATION_TYPE, findQQGroupByName.type);
                        intent2.putExtra(QQMessage.KEY_CONVERSATION_ID, findQQGroupByName.id);
                        intent2.putExtra(QQMessage.KEY_CONVERSATION_NAME, findQQGroupByName.name);
                        intent2.putExtra("open_chatfragment", true);
                        intent2.putExtra("entrance", 6);
                    }
                    notification.contentIntent = g.a(getApplicationContext(), NotificationUtil.getUniqueNotificationId(), intent2, 1073741824, (Bundle) null, pendingIntent.getCreatorUserHandle());
                    try {
                        qQMessage2 = new QQMessage(this, new com.miui.luckymoney.model.Notification(statusBarNotification.getPackageName(), NotificationUtil.getUniqueNotificationId(), (String) null, notification));
                    } catch (Exception e) {
                        Log.e(TAG, "failed to create qqmessage object", e);
                        qQMessage2 = null;
                    }
                    if (qQMessage2 != null) {
                        qQMessage.treatedAsGroupMessage = true;
                        qQMessage.conversationName = str3;
                        if (qQMessage.isHongbao()) {
                            Log.d(TAG, "qq message is lucky money message, continue");
                            this.mMainHandler.post(new Runnable() {
                                public void run() {
                                    if (PhoneStateMonitor.isPhoneBusy(LuckyMoneyMonitorService.this)) {
                                        Log.d(LuckyMoneyMonitorService.TAG, "phony is busy, do not remind qq lucky money");
                                    } else if (LuckyMoneyMonitorService.this.mQQPipeline != null) {
                                        LuckyMoneyMonitorService.this.mQQPipeline.process(qQMessage);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    private void registerCloudControlReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(this.mCloudControlReceiver, intentFilter);
    }

    private void registerConfigChangedReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(com.miui.luckymoney.config.Constants.ACTION_CONFIG_CHANGED_BROADCAST);
        registerReceiver(this.mConfigChangedReceiver, intentFilter);
    }

    private void registerConfigUpdateReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        intentFilter.addAction(com.miui.luckymoney.config.Constants.ACTION_UPDATE_TIPS_CONFIG);
        intentFilter.addAction(com.miui.luckymoney.config.Constants.ACTION_UPDATE_ALARM_CONFIG);
        intentFilter.addAction(com.miui.luckymoney.config.Constants.ACTION_FLOAT_TIPS_ACTIVITY_END);
        registerReceiver(this.mConfigUpdateReceiver, intentFilter);
    }

    private void registerLuckyAlarmReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(com.miui.luckymoney.config.Constants.ACTION_LUCKY_ALARM);
        registerReceiver(this.mLuckyAlarmReceiver, intentFilter);
    }

    private void registerPackageReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.System.ACTION_PACKAGE_ADDED);
        intentFilter.addDataScheme("package");
        registerReceiver(this.mPackageReceiver, intentFilter);
    }

    private void registerPhoneStateMonitor() {
        PhoneStateMonitor.registerListener(this.mPhoneStateMonitor);
    }

    private void registerRefreshFloatTipsDailyReceiver() {
        registerReceiver(this.mRefreshFloatTipsDailyReceiver, new IntentFilter(com.miui.luckymoney.config.Constants.ACTION_REFRESH_FLOAT_TIPS_DAILY));
    }

    private void registerScreenReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.System.ACTION_SCREEN_ON);
        intentFilter.addAction(Constants.System.ACTION_SCREEN_OFF);
        intentFilter.addAction(Constants.System.ACTION_USER_PRESENT);
        registerReceiver(this.mLockScreenReceiver, intentFilter);
    }

    /* access modifiers changed from: private */
    public void removeAssistantFloatView() {
        synchronized (this.mFloatViewLock) {
            if (this.mAssistantFloatView != null) {
                this.mAssistantFloatView.removeFloatView();
                this.mAssistantFloatView = null;
            }
        }
    }

    private void resetAlarmStatus() {
        new LuckyAlarmResult(this.mCommonConfig.getLuckyAlarmConfig(), true).disableAllItemTimer();
    }

    private boolean shouldPerparFloatTipsAlerm() {
        return System.currentTimeMillis() < this.mCommonConfig.getFloatTipsStopTime();
    }

    private void unRegisterCloudControlReceiver() {
        unregisterReceiver(this.mCloudControlReceiver);
    }

    private void unRegisterConfigUpdateReceiver() {
        unregisterReceiver(this.mConfigUpdateReceiver);
    }

    private void unRegisterLuckyAlarmReceiver() {
        unregisterReceiver(this.mLuckyAlarmReceiver);
    }

    private void unRegisterPackageReceiver() {
        unregisterReceiver(this.mPackageReceiver);
    }

    private void unRegisterRefreshFloatTipsDailyReceiver() {
        unregisterReceiver(this.mRefreshFloatTipsDailyReceiver);
    }

    private void unRegisterScreenReceiver() {
        unregisterReceiver(this.mLockScreenReceiver);
    }

    private void unregisterConfigChangedReceiver() {
        unregisterReceiver(this.mConfigChangedReceiver);
    }

    private void unregisterPhoneStateMonitor() {
        PhoneStateMonitor.unregisterListener(this.mPhoneStateMonitor);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        this.mContext = getApplicationContext();
        this.mCommonConfig = CommonConfig.getInstance(this.mContext);
        this.mMainHandler = new Handler(Looper.getMainLooper(), this.mHandlerCallback);
        this.mMessageConfig = new DefaultConfiguration(this);
        this.mQQConfig = new QQConfiguration(this);
        this.mMiTalkConfig = new MiTalkConfiguration(this);
        this.mBusinessConfig = new BusinessConfiguration(this);
        BaseConfiguration baseConfiguration = this.mMessageConfig;
        this.mWeixinPipeline = Pipeline.create(baseConfiguration, new GeneralMessageViewCreator(baseConfiguration));
        BaseConfiguration baseConfiguration2 = this.mQQConfig;
        this.mQQPipeline = Pipeline.create(baseConfiguration2, new GeneralMessageViewCreator(baseConfiguration2));
        BaseConfiguration baseConfiguration3 = this.mMiTalkConfig;
        this.mMitalkPipeline = Pipeline.create(baseConfiguration3, new GeneralMessageViewCreator(baseConfiguration3));
        BaseConfiguration baseConfiguration4 = this.mBusinessConfig;
        this.mBusinessPipeline = Pipeline.create(baseConfiguration4, new GeneralMessageViewCreator(baseConfiguration4));
        this.mPhoneStateMonitor = new PhoneRingMonitor();
        registerConfigChangedReceiver();
        if (this.mCommonConfig.isDesktopFloatWindowEnable()) {
            addAssistantFloatView();
        }
        registerScreenReceiver();
        registerConfigUpdateReceiver();
        registerCloudControlReceiver();
        registerLuckyAlarmReceiver();
        registerPackageReceiver();
        PhoneStateMonitor.startMonitor(this.mContext);
        registerPhoneStateMonitor();
        resetAlarmStatus();
        initRefreshFloatTipsDaily();
        registerRefreshFloatTipsDailyReceiver();
        bindService(new Intent(this, NotificationListener.class), this.mNotificationListenerConn, 1);
    }

    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        unRegisterConfigUpdateReceiver();
        unRegisterCloudControlReceiver();
        unRegisterLuckyAlarmReceiver();
        unRegisterPackageReceiver();
        unRegisterScreenReceiver();
        removeAssistantFloatView();
        unregisterConfigChangedReceiver();
        unregisterPhoneStateMonitor();
        unRegisterRefreshFloatTipsDailyReceiver();
        PhoneStateMonitor.stopMonitor(this.mContext);
        Pipeline.recycle(this.mMitalkPipeline);
        Pipeline.recycle(this.mWeixinPipeline);
        Pipeline.recycle(this.mQQPipeline);
        Pipeline.recycle(this.mBusinessPipeline);
        this.mMitalkPipeline = null;
        this.mWeixinPipeline = null;
        this.mQQPipeline = null;
        this.mBusinessPipeline = null;
        unbindService(this.mNotificationListenerConn);
        super.onDestroy();
    }

    public void onNotificationPosted(StatusBarNotification statusBarNotification) {
        Handler handler;
        Runnable runnable;
        final BusinessMessage businessMessage;
        final MiTalkMessage miTalkMessage;
        final QQMessage qQMessage;
        PendingIntent pendingIntent;
        final WechatMessage wechatMessage;
        if (this.mMainHandler != null) {
            String packageName = statusBarNotification.getPackageName();
            if (AppConstants.Package.PACKAGE_NAME_MM.equals(packageName)) {
                Log.d(TAG, "received a mm message");
                try {
                    wechatMessage = new WechatMessage(this, new com.miui.luckymoney.model.Notification(statusBarNotification.getPackageName(), statusBarNotification.getId(), statusBarNotification.getTag(), statusBarNotification.getNotification()));
                } catch (Exception e) {
                    Log.e(TAG, "failed to create WechatMessage object", e);
                    wechatMessage = null;
                }
                if (wechatMessage != null && wechatMessage.isHongbao()) {
                    Log.d(TAG, "mm message is lucky money message, continue");
                    handler = this.mMainHandler;
                    runnable = new Runnable() {
                        public void run() {
                            if (PhoneStateMonitor.isPhoneBusy(LuckyMoneyMonitorService.this.mContext)) {
                                Log.d(LuckyMoneyMonitorService.TAG, "phone is busy, do not remind mm lucky monkey");
                            } else if (LuckyMoneyMonitorService.this.mWeixinPipeline != null) {
                                LuckyMoneyMonitorService.this.mWeixinPipeline.process(wechatMessage);
                            }
                        }
                    };
                } else {
                    return;
                }
            } else if (AppConstants.Package.PACKAGE_NAME_QQ.equals(packageName)) {
                Log.d(TAG, "received a qq message");
                try {
                    Notification notification = statusBarNotification.getNotification();
                    if (notification != null && (pendingIntent = notification.contentIntent) != null) {
                        c.a a2 = c.a.a((Object) pendingIntent);
                        a2.a("getIntent", (Class<?>[]) null, new Object[0]);
                        Intent intent = (Intent) a2.d();
                        if (intent != null) {
                            notification.contentIntent = g.a(getApplicationContext(), NotificationUtil.getUniqueNotificationId(), (Intent) intent.clone(), 1073741824, (Bundle) null, pendingIntent.getCreatorUserHandle());
                            qQMessage = new QQMessage(this, new com.miui.luckymoney.model.Notification(statusBarNotification.getPackageName(), statusBarNotification.getId(), statusBarNotification.getTag(), notification));
                            if (qQMessage != null) {
                                QQGroupCollector.collect(this, qQMessage);
                                if (qQMessage.type == -1 || !qQMessage.isHongbao()) {
                                    processTypeUnknownNotification(statusBarNotification, qQMessage);
                                    return;
                                }
                                Log.d(TAG, "qq message is lucky money message, continue");
                                this.mMainHandler.post(new Runnable() {
                                    public void run() {
                                        if (PhoneStateMonitor.isPhoneBusy(LuckyMoneyMonitorService.this.mContext)) {
                                            Log.d(LuckyMoneyMonitorService.TAG, "phone is busy, do not remind qq lucky monkey");
                                        } else if (LuckyMoneyMonitorService.this.mQQPipeline != null) {
                                            LuckyMoneyMonitorService.this.mQQPipeline.process(qQMessage);
                                        }
                                    }
                                });
                                return;
                            }
                            return;
                        }
                        return;
                    }
                    return;
                } catch (Exception e2) {
                    Log.e(TAG, "failed to create QQMessage object", e2);
                    qQMessage = null;
                }
            } else if (AppConstants.Package.PACKAGE_NAME_MITALK.equals(packageName)) {
                try {
                    miTalkMessage = new MiTalkMessage(this, new com.miui.luckymoney.model.Notification(statusBarNotification.getPackageName(), statusBarNotification.getId(), statusBarNotification.getTag(), statusBarNotification.getNotification()));
                } catch (Exception e3) {
                    Log.e(TAG, "failed to create MiTalkMessage object", e3);
                    miTalkMessage = null;
                }
                if (miTalkMessage != null && miTalkMessage.isHongbao()) {
                    Log.d(TAG, "mitalk message is lucky money message, continue");
                    handler = this.mMainHandler;
                    runnable = new Runnable() {
                        public void run() {
                            if (PhoneStateMonitor.isPhoneBusy(LuckyMoneyMonitorService.this.mContext)) {
                                Log.d(LuckyMoneyMonitorService.TAG, "phone is busy, do not remind mitalk lunky monkey");
                            } else if (LuckyMoneyMonitorService.this.mMitalkPipeline != null) {
                                LuckyMoneyMonitorService.this.mMitalkPipeline.process(miTalkMessage);
                            }
                        }
                    };
                } else {
                    return;
                }
            } else if ("com.xiaomi.xmsf".equals(packageName)) {
                try {
                    businessMessage = new BusinessMessage(this, new com.miui.luckymoney.model.Notification(statusBarNotification.getPackageName(), statusBarNotification.getId(), statusBarNotification.getTag(), statusBarNotification.getNotification()));
                } catch (Exception e4) {
                    Log.e(TAG, "failed to create BusinessMessage object", e4);
                    businessMessage = null;
                }
                if (businessMessage != null && businessMessage.isHongbao()) {
                    Log.d(TAG, "business message is lucky money message, continue");
                    handler = this.mMainHandler;
                    runnable = new Runnable() {
                        public void run() {
                            if (PhoneStateMonitor.isPhoneBusy(LuckyMoneyMonitorService.this.mContext)) {
                                Log.d(LuckyMoneyMonitorService.TAG, "phone is busy, do not remind business lunky monkey");
                            } else if (LuckyMoneyMonitorService.this.mBusinessPipeline != null) {
                                LuckyMoneyMonitorService.this.mBusinessPipeline.process(businessMessage);
                            }
                        }
                    };
                } else {
                    return;
                }
            } else {
                return;
            }
            handler.post(runnable);
        }
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        return 1;
    }
}
