package com.miui.networkassistant.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import b.b.c.c.a.a;
import b.b.c.h.f;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.NotificationUtil;
import com.miui.networkassistant.utils.TelephonyUtil;
import com.miui.networkassistant.webapi.WebApiAccessHelper;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import miui.security.SecurityManager;
import org.json.JSONArray;

public class TcSmsReportService extends Service {
    private static final String TAG = "TcSmsReportService";
    /* access modifiers changed from: private */
    public int mCurrentSlotNum;
    /* access modifiers changed from: private */
    public ArrayList<SmsReportListener> mListenersSelfLocked = new ArrayList<>();
    /* access modifiers changed from: private */
    public int mReportSmsType = -1;
    private BroadcastReceiver mReportStatusReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            TcSmsReportService.this.stopSelf();
        }
    };
    /* access modifiers changed from: private */
    public SecurityManager mSecurityManager;
    /* access modifiers changed from: private */
    public String mSmsAllStr;
    /* access modifiers changed from: private */
    public String mSmsDirection;
    /* access modifiers changed from: private */
    public ArrayList<String> mSmsListSelfLocked = new ArrayList<>();
    /* access modifiers changed from: private */
    public String mSmsNum;
    /* access modifiers changed from: private */
    public String mSmsReceiveNum;
    private BroadcastReceiver mSmsReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String str;
            SmsMessage[] messagesFromIntent = TelephonyUtil.getMessagesFromIntent(intent);
            if (messagesFromIntent != null && messagesFromIntent.length != 0 && TextUtils.equals(TelephonyUtil.removePhoneNumPrefix(messagesFromIntent[0].getDisplayOriginatingAddress(), "+86"), TcSmsReportService.this.mSmsReceiveNum)) {
                if (messagesFromIntent.length == 1) {
                    str = messagesFromIntent[0].getDisplayMessageBody();
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (SmsMessage displayMessageBody : messagesFromIntent) {
                        sb.append(displayMessageBody.getDisplayMessageBody());
                    }
                    str = sb.toString();
                }
                TcSmsReportService.this.onSmsReceived(str);
                abortBroadcast();
                if (DeviceUtil.IS_KITKAT_OR_LATER) {
                    setResultCode(0);
                }
            }
        }
    };
    BroadcastReceiver mSmsSentReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (getResultCode() != -1) {
                TcSmsReportService.this.onSmsSentFailure();
            }
        }
    };
    /* access modifiers changed from: private */
    public SmsReportStatus mStatus = SmsReportStatus.Init;
    private Timer mTimer;
    private TimerTask mTimerTask;

    public interface SmsReportListener {
        void onSmsReceived();

        void onSmsSentFailure();

        void onTimeOut();
    }

    public enum SmsReportStatus {
        Init,
        Receiving,
        Timeout,
        SmsSendFailure,
        Received
    }

    public class TcSmsReportServiceBinder extends Binder {
        public TcSmsReportServiceBinder() {
        }

        public int getCurrentSlotNum() {
            return TcSmsReportService.this.mCurrentSlotNum;
        }

        public int getReportSmsType() {
            return TcSmsReportService.this.mReportSmsType;
        }

        public String getSmsDirection() {
            return TcSmsReportService.this.mSmsDirection;
        }

        public String getSmsNum() {
            return TcSmsReportService.this.mSmsNum;
        }

        public String getSmsReceiveNum() {
            return TcSmsReportService.this.mSmsReceiveNum;
        }

        public String getSmsReturned() {
            return TcSmsReportService.this.mSmsAllStr;
        }

        public SmsReportStatus getStatus() {
            return TcSmsReportService.this.mStatus;
        }

        public void registerSmsReportListener(SmsReportListener smsReportListener) {
            synchronized (TcSmsReportService.this.mListenersSelfLocked) {
                TcSmsReportService.this.mListenersSelfLocked.add(smsReportListener);
            }
        }

        public void report(int i) {
            int i2;
            Context context;
            final SimUserInfo instance = SimUserInfo.getInstance(TcSmsReportService.this.getApplicationContext(), TcSmsReportService.this.mCurrentSlotNum);
            final JSONArray jSONArray = new JSONArray();
            synchronized (TcSmsReportService.this.mSmsListSelfLocked) {
                Iterator it = TcSmsReportService.this.mSmsListSelfLocked.iterator();
                while (it.hasNext()) {
                    jSONArray.put((String) it.next());
                }
            }
            final boolean j = f.j(TcSmsReportService.this.getApplicationContext());
            if (j) {
                context = TcSmsReportService.this.getApplicationContext();
                i2 = R.string.tc_sms_report_upload_success;
            } else {
                context = TcSmsReportService.this.getApplicationContext();
                i2 = R.string.tc_sms_report_upload_when_net;
            }
            Toast.makeText(context, i2, 1).show();
            final int i3 = i;
            a.a(new Runnable() {
                public void run() {
                    boolean z = true;
                    if (j) {
                        z = true ^ WebApiAccessHelper.reportTrafficCorrectionSms(TcSmsReportService.this.mSmsNum, TcSmsReportService.this.mSmsDirection, TcSmsReportService.this.mSmsReceiveNum, jSONArray.toString(), String.valueOf(instance.getProvince()), String.valueOf(instance.getCity()), instance.getOperator(), String.valueOf(instance.getBrand()), String.valueOf(i3), (String) null).isSuccess();
                    }
                    if (z) {
                        JSONArray jSONArray = new JSONArray();
                        jSONArray.put(TcSmsReportService.this.mSmsNum);
                        jSONArray.put(TcSmsReportService.this.mSmsDirection);
                        jSONArray.put(TcSmsReportService.this.mSmsReceiveNum);
                        jSONArray.put(jSONArray.toString());
                        jSONArray.put(instance.getProvince());
                        jSONArray.put(instance.getCity());
                        jSONArray.put(instance.getOperator());
                        jSONArray.put(instance.getBrand());
                        jSONArray.put(i3);
                        SimUserInfo.getInstance(TcSmsReportService.this.getApplicationContext(), TcSmsReportService.this.mCurrentSlotNum).setTcSmsReportCache(jSONArray.toString());
                    }
                    TcSmsReportService.this.stopSelf();
                }
            });
        }

        public void reset() {
            SmsReportStatus unused = TcSmsReportService.this.mStatus = SmsReportStatus.Init;
            synchronized (TcSmsReportService.this.mSmsListSelfLocked) {
                TcSmsReportService.this.mSmsListSelfLocked.clear();
                String unused2 = TcSmsReportService.this.mSmsAllStr = null;
            }
            TcSmsReportService.this.stopSelf();
        }

        public void startMonitorSms(String str, String str2, String str3, int i, int i2) {
            if (DeviceUtil.IS_KITKAT_OR_LATER) {
                TcSmsReportService.this.mSecurityManager.startInterceptSmsBySender(TcSmsReportService.this.getApplicationContext(), str3, 5);
            }
            SmsReportStatus unused = TcSmsReportService.this.mStatus = SmsReportStatus.Receiving;
            String unused2 = TcSmsReportService.this.mSmsNum = str;
            String unused3 = TcSmsReportService.this.mSmsDirection = str2;
            int unused4 = TcSmsReportService.this.mReportSmsType = i2;
            int unused5 = TcSmsReportService.this.mCurrentSlotNum = i;
            String unused6 = TcSmsReportService.this.mSmsReceiveNum = str3;
            TcSmsReportService.this.sendSms(str, str2, i);
            TcSmsReportService.this.startTimerTask();
        }

        public void unRegisterSmsReportListener(SmsReportListener smsReportListener) {
            synchronized (TcSmsReportService.this.mListenersSelfLocked) {
                TcSmsReportService.this.mListenersSelfLocked.remove(smsReportListener);
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0037, code lost:
        r5.mSmsAllStr = r0.toString();
        r6 = r5.mListenersSelfLocked;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x003f, code lost:
        monitor-enter(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        r5.mStatus = com.miui.networkassistant.service.TcSmsReportService.SmsReportStatus.Received;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x004a, code lost:
        if (r5.mListenersSelfLocked.isEmpty() != false) goto L_0x0062;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x004c, code lost:
        r0 = r5.mListenersSelfLocked.iterator();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0056, code lost:
        if (r0.hasNext() == false) goto L_0x0079;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0058, code lost:
        r0.next().onSmsReceived();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0062, code lost:
        com.miui.networkassistant.utils.NotificationUtil.sendTcSmsReceivedNotify(getApplicationContext(), getString(com.miui.securitycenter.R.string.tc_sms_report_get_success_notify_title), getString(com.miui.securitycenter.R.string.tc_sms_report_get_success_notify_body), r5.mCurrentSlotNum);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0079, code lost:
        monitor-exit(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x007a, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onSmsReceived(java.lang.String r6) {
        /*
            r5 = this;
            r5.stopReceiving()
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.util.ArrayList<java.lang.String> r1 = r5.mSmsListSelfLocked
            monitor-enter(r1)
            java.util.ArrayList<java.lang.String> r2 = r5.mSmsListSelfLocked     // Catch:{ all -> 0x007e }
            java.util.Iterator r2 = r2.iterator()     // Catch:{ all -> 0x007e }
        L_0x0011:
            boolean r3 = r2.hasNext()     // Catch:{ all -> 0x007e }
            if (r3 == 0) goto L_0x002e
            java.lang.Object r3 = r2.next()     // Catch:{ all -> 0x007e }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ all -> 0x007e }
            boolean r4 = android.text.TextUtils.equals(r6, r3)     // Catch:{ all -> 0x007e }
            if (r4 == 0) goto L_0x0025
            monitor-exit(r1)     // Catch:{ all -> 0x007e }
            return
        L_0x0025:
            r0.append(r3)     // Catch:{ all -> 0x007e }
            java.lang.String r3 = "\n________________________\n\n"
            r0.append(r3)     // Catch:{ all -> 0x007e }
            goto L_0x0011
        L_0x002e:
            java.util.ArrayList<java.lang.String> r2 = r5.mSmsListSelfLocked     // Catch:{ all -> 0x007e }
            r2.add(r6)     // Catch:{ all -> 0x007e }
            r0.append(r6)     // Catch:{ all -> 0x007e }
            monitor-exit(r1)     // Catch:{ all -> 0x007e }
            java.lang.String r6 = r0.toString()
            r5.mSmsAllStr = r6
            java.util.ArrayList<com.miui.networkassistant.service.TcSmsReportService$SmsReportListener> r6 = r5.mListenersSelfLocked
            monitor-enter(r6)
            com.miui.networkassistant.service.TcSmsReportService$SmsReportStatus r0 = com.miui.networkassistant.service.TcSmsReportService.SmsReportStatus.Received     // Catch:{ all -> 0x007b }
            r5.mStatus = r0     // Catch:{ all -> 0x007b }
            java.util.ArrayList<com.miui.networkassistant.service.TcSmsReportService$SmsReportListener> r0 = r5.mListenersSelfLocked     // Catch:{ all -> 0x007b }
            boolean r0 = r0.isEmpty()     // Catch:{ all -> 0x007b }
            if (r0 != 0) goto L_0x0062
            java.util.ArrayList<com.miui.networkassistant.service.TcSmsReportService$SmsReportListener> r0 = r5.mListenersSelfLocked     // Catch:{ all -> 0x007b }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x007b }
        L_0x0052:
            boolean r1 = r0.hasNext()     // Catch:{ all -> 0x007b }
            if (r1 == 0) goto L_0x0079
            java.lang.Object r1 = r0.next()     // Catch:{ all -> 0x007b }
            com.miui.networkassistant.service.TcSmsReportService$SmsReportListener r1 = (com.miui.networkassistant.service.TcSmsReportService.SmsReportListener) r1     // Catch:{ all -> 0x007b }
            r1.onSmsReceived()     // Catch:{ all -> 0x007b }
            goto L_0x0052
        L_0x0062:
            android.content.Context r0 = r5.getApplicationContext()     // Catch:{ all -> 0x007b }
            r1 = 2131758249(0x7f100ca9, float:1.9147457E38)
            java.lang.String r1 = r5.getString(r1)     // Catch:{ all -> 0x007b }
            r2 = 2131758248(0x7f100ca8, float:1.9147455E38)
            java.lang.String r2 = r5.getString(r2)     // Catch:{ all -> 0x007b }
            int r3 = r5.mCurrentSlotNum     // Catch:{ all -> 0x007b }
            com.miui.networkassistant.utils.NotificationUtil.sendTcSmsReceivedNotify(r0, r1, r2, r3)     // Catch:{ all -> 0x007b }
        L_0x0079:
            monitor-exit(r6)     // Catch:{ all -> 0x007b }
            return
        L_0x007b:
            r0 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x007b }
            throw r0
        L_0x007e:
            r6 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x007e }
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.service.TcSmsReportService.onSmsReceived(java.lang.String):void");
    }

    /* access modifiers changed from: private */
    public void onSmsSentFailure() {
        stopReceiving();
        synchronized (this.mListenersSelfLocked) {
            this.mStatus = SmsReportStatus.SmsSendFailure;
            if (!this.mListenersSelfLocked.isEmpty()) {
                Iterator<SmsReportListener> it = this.mListenersSelfLocked.iterator();
                while (it.hasNext()) {
                    it.next().onSmsSentFailure();
                }
            } else {
                NotificationUtil.sendTcSmsTimeOutOrFailureNotify(getApplicationContext(), getString(R.string.tc_sms_report_notify_get_failure_title), getString(R.string.tc_sms_report_notify_get_failure_body), this.mCurrentSlotNum);
            }
        }
    }

    /* access modifiers changed from: private */
    public void onTimeOut() {
        stopReceiving();
        synchronized (this.mListenersSelfLocked) {
            this.mStatus = SmsReportStatus.Timeout;
            if (!this.mListenersSelfLocked.isEmpty()) {
                Iterator<SmsReportListener> it = this.mListenersSelfLocked.iterator();
                while (it.hasNext()) {
                    it.next().onTimeOut();
                }
            } else {
                NotificationUtil.sendTcSmsTimeOutOrFailureNotify(getApplicationContext(), getString(R.string.tc_sms_report_notify_get_timeout_title), getString(R.string.tc_sms_report_notify_get_timeout_body), this.mCurrentSlotNum);
            }
        }
    }

    private void registerReportStatusReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.App.ACTION_BROADCAST_TC_SMS_REPORT_STATUS);
        registerReceiver(this.mReportStatusReceiver, intentFilter);
    }

    private void registerSmsReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.System.ACTION_SMS_RECEIVED);
        intentFilter.setPriority(Integer.MAX_VALUE);
        intentFilter.addCategory(Constants.System.CATEGORY_DEFALUT);
        registerReceiver(this.mSmsReceiver, intentFilter, Constants.System.PERMISSION_BROADCAST_SMS, (Handler) null);
    }

    private void registerSmsSendedReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.System.SMS_RECEIVER_ACTION);
        registerReceiver(this.mSmsSentReceiver, intentFilter);
    }

    /* access modifiers changed from: private */
    public void sendSms(String str, String str2, int i) {
        registerSmsSendedReceiver();
        Intent intent = new Intent();
        intent.setAction(Constants.System.SMS_RECEIVER_ACTION);
        TelephonyUtil.sendTextMessage(str, (String) null, str2, PendingIntent.getBroadcast(this, 0, intent, 0), (PendingIntent) null, i);
    }

    /* access modifiers changed from: private */
    public void startTimerTask() {
        this.mTimer = new Timer();
        this.mTimerTask = new TimerTask() {
            public void run() {
                TcSmsReportService.this.onTimeOut();
            }
        };
        this.mTimer.schedule(this.mTimerTask, 120000);
    }

    private void stopReceiving() {
        if (DeviceUtil.IS_KITKAT_OR_LATER) {
            this.mSecurityManager.stopInterceptSmsBySender();
        }
        stopTimerTask();
    }

    private void stopTimerTask() {
        Timer timer = this.mTimer;
        if (timer != null) {
            timer.cancel();
        }
    }

    private void unRegisterReportStatusReceiver() {
        unregisterReceiver(this.mReportStatusReceiver);
    }

    private void unRegisterSmsReceiver() {
        unregisterReceiver(this.mSmsReceiver);
    }

    private void unRegisterSmsSendedReceiver() {
        try {
            if (this.mSmsSentReceiver != null) {
                unregisterReceiver(this.mSmsSentReceiver);
            }
        } catch (IllegalArgumentException e) {
            Log.i(TAG, "unRegisterSmsSendedReceiver", e);
        }
    }

    public IBinder onBind(Intent intent) {
        return new TcSmsReportServiceBinder();
    }

    public void onCreate() {
        super.onCreate();
        if (DeviceUtil.IS_KITKAT_OR_LATER) {
            this.mSecurityManager = (SecurityManager) getSystemService("security");
        }
        registerSmsSendedReceiver();
        registerSmsReceiver();
        registerReportStatusReceiver();
    }

    public void onDestroy() {
        unRegisterSmsSendedReceiver();
        unRegisterSmsReceiver();
        unRegisterReportStatusReceiver();
        super.onDestroy();
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        return super.onStartCommand(intent, 1, i2);
    }
}
