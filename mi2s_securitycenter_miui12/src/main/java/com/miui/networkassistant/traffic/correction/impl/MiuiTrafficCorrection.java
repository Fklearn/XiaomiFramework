package com.miui.networkassistant.traffic.correction.impl;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.PowerManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.h.f;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.model.TrafficUsedStatus;
import com.miui.networkassistant.traffic.correction.ITrafficCorrection;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.TelephonyUtil;
import com.miui.sdk.tc.DataUsage;
import com.miui.sdk.tc.TcDirection;
import com.miui.sdk.tc.TcManager;
import com.miui.sdk.tc.UserConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;
import miui.security.SecurityManager;
import org.json.JSONException;

public class MiuiTrafficCorrection implements ITrafficCorrection, ITrafficCorrection.TrafficCorrectionListener {
    private static final String TAG = "MiuiTrafficCorrection";
    private static MiuiTrafficCorrection sInstance;
    private static HashMap<String, MiuiTrafficCorrection> sInstanceMap;
    private List<TcDirection> mAllDirection;
    private Context mContext;
    private TcDirection mCurrentTcDirection;
    private int mCurrentTcIndex;
    private int mCurrentTcType = 1;
    private Set<TcDirection> mDirectionCache;
    private int mDirectionSize;
    private int mDirectionType;
    private String mImsi;
    private boolean mIsBackground;
    private boolean mIsFinished = true;
    private boolean mIsUpdated = true;
    private ArrayList<ITrafficCorrection.TrafficCorrectionListener> mListeners = new ArrayList<>();
    private PowerManager mPowerManager;
    private int mSlotNum;
    private SmsFilter mSmsFilter;
    private final Object mSmsFilterLock = new Object();
    private boolean mSmsSendRegister;
    private BroadcastReceiver mSmsSentReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (getResultCode() != -1) {
                MiuiTrafficCorrection.this.onTrafficCorrected(new TrafficUsedStatus(1));
            }
        }
    };
    private TcManager mTcManager;
    private Timer mTimer;
    private final Object mTimerLock = new Object();
    private long mTotalLimit;
    private PowerManager.WakeLock mWakeLock;

    private static class SmsFilter {
        private Context mContext;
        private ITrafficCorrection.TrafficCorrectionListener mListener;
        private Object mLock = new Object();
        private SmsReceiver mReceiver;
        private SecurityManager mSecurityManager;

        public SmsFilter(Context context, ITrafficCorrection.TrafficCorrectionListener trafficCorrectionListener) {
            this.mContext = context;
            this.mListener = trafficCorrectionListener;
            if (DeviceUtil.IS_KITKAT_OR_LATER) {
                this.mSecurityManager = (SecurityManager) this.mContext.getSystemService("security");
            }
        }

        public void regist(int i, String str, int i2, int i3) {
            synchronized (this.mLock) {
                unregist();
                this.mReceiver = new SmsReceiver(this.mListener, i, i2, i3);
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(Constants.System.ACTION_SMS_RECEIVED);
                intentFilter.setPriority(Integer.MAX_VALUE);
                intentFilter.addCategory(Constants.System.CATEGORY_DEFALUT);
                this.mContext.registerReceiver(this.mReceiver, intentFilter, Constants.System.PERMISSION_BROADCAST_SMS, (Handler) null);
            }
            if (DeviceUtil.IS_KITKAT_OR_LATER) {
                this.mSecurityManager.startInterceptSmsBySender(this.mContext, str, i2);
            }
        }

        public void unregist() {
            if (DeviceUtil.IS_KITKAT_OR_LATER) {
                this.mSecurityManager.stopInterceptSmsBySender();
            }
            try {
                synchronized (this.mLock) {
                    if (this.mReceiver != null) {
                        this.mContext.getApplicationContext().unregisterReceiver(this.mReceiver);
                        this.mReceiver = null;
                    }
                }
            } catch (IllegalArgumentException e) {
                Log.i(MiuiTrafficCorrection.TAG, "SmsFilter unregister", e);
            }
        }
    }

    private static class SmsReceiver extends BroadcastReceiver {
        private int mInterceptedSmsCount;
        /* access modifiers changed from: private */
        public ITrafficCorrection.TrafficCorrectionListener mListener;
        /* access modifiers changed from: private */
        public int mSlotNum;
        /* access modifiers changed from: private */
        public int mTcType;

        public SmsReceiver(ITrafficCorrection.TrafficCorrectionListener trafficCorrectionListener, int i, int i2, int i3) {
            this.mListener = trafficCorrectionListener;
            this.mSlotNum = i;
            this.mInterceptedSmsCount = i2;
            this.mTcType = i3;
        }

        private void doAbortBroadcast() {
            abortBroadcast();
            if (DeviceUtil.IS_KITKAT_OR_LATER) {
                setResultCode(0);
            }
        }

        public void onReceive(Context context, Intent intent) {
            final String str;
            Log.i(MiuiTrafficCorrection.TAG, "LRL SmsReceiver onReceive");
            this.mInterceptedSmsCount--;
            try {
                int intExtra = intent.getIntExtra("slot_id", 0);
                if (intExtra == this.mSlotNum) {
                    SmsMessage[] messagesFromIntent = TelephonyUtil.getMessagesFromIntent(intent);
                    if (messagesFromIntent != null) {
                        if (messagesFromIntent.length != 0) {
                            final String displayOriginatingAddress = messagesFromIntent[0].getDisplayOriginatingAddress();
                            if (TcManager.getInstance().isSmsNeedBlock(displayOriginatingAddress, this.mSlotNum)) {
                                if (messagesFromIntent.length == 1) {
                                    str = messagesFromIntent[0].getDisplayMessageBody();
                                } else {
                                    StringBuilder sb = new StringBuilder();
                                    for (SmsMessage displayMessageBody : messagesFromIntent) {
                                        sb.append(displayMessageBody.getDisplayMessageBody());
                                    }
                                    str = sb.toString();
                                }
                                SimUserInfo instance = SimUserInfo.getInstance(context, intExtra);
                                if (this.mTcType == 1) {
                                    instance.setTrafficSmsDetail(str);
                                } else if (this.mTcType == 2) {
                                    instance.setBillSmsDetail(str);
                                }
                                final boolean z = this.mInterceptedSmsCount > 0;
                                if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(displayOriginatingAddress)) {
                                    if (Pattern.matches("[\\s\\S]*\\d[\\s\\S]*", str)) {
                                        doAbortBroadcast();
                                        new AsyncTask<Void, Void, Void>() {
                                            /* access modifiers changed from: protected */
                                            public Void doInBackground(Void... voidArr) {
                                                DataUsage result = TcManager.getInstance().getResult(displayOriginatingAddress, str, SmsReceiver.this.mSlotNum, SmsReceiver.this.mTcType);
                                                if (result.getReturnCode() == 0) {
                                                    TrafficUsedStatus trafficUsedStatus = new TrafficUsedStatus(4);
                                                    DataUsage.PackageDetail dailyPkgDetail = result.getDailyPkgDetail();
                                                    if (dailyPkgDetail != null) {
                                                        trafficUsedStatus.setReturnCode(0);
                                                        trafficUsedStatus.setTotalTrafficB(dailyPkgDetail.getTotalTrafficB());
                                                        trafficUsedStatus.setUsedTrafficB(dailyPkgDetail.getUsedTrafficB());
                                                        trafficUsedStatus.setRemainTrafficB(dailyPkgDetail.getRemainTrafficB());
                                                        trafficUsedStatus.setNormalStable(dailyPkgDetail.isStable());
                                                        trafficUsedStatus.setJustOver(dailyPkgDetail.isJustOver());
                                                    }
                                                    DataUsage.PackageDetail leisurePkgDetail = result.getLeisurePkgDetail();
                                                    if (leisurePkgDetail != null) {
                                                        trafficUsedStatus.setLeisureTotalB(leisurePkgDetail.getTotalTrafficB());
                                                        trafficUsedStatus.setLeisureUsedB(leisurePkgDetail.getUsedTrafficB());
                                                        trafficUsedStatus.setLeisureRemainB(leisurePkgDetail.getRemainTrafficB());
                                                        trafficUsedStatus.setLeisureEnable(true);
                                                        trafficUsedStatus.setLeisureStable(leisurePkgDetail.isStable());
                                                    }
                                                    DataUsage.PackageDetail extraPkgDetail = result.getExtraPkgDetail();
                                                    if (extraPkgDetail != null) {
                                                        trafficUsedStatus.setExtraTotalB(extraPkgDetail.getTotalTrafficB());
                                                        trafficUsedStatus.setExtraUsedB(extraPkgDetail.getUsedTrafficB());
                                                        trafficUsedStatus.setExtraRemainB(extraPkgDetail.getRemainTrafficB());
                                                        trafficUsedStatus.setExtraEnable(true);
                                                        trafficUsedStatus.setExtraStable(extraPkgDetail.isStable());
                                                    }
                                                    DataUsage.PackageDetail billPkg = result.getBillPkg();
                                                    if (billPkg != null) {
                                                        trafficUsedStatus.setReturnCode(0);
                                                        trafficUsedStatus.setBillEnabled(true);
                                                        trafficUsedStatus.setBillTotal(billPkg.getTotalTrafficB());
                                                        trafficUsedStatus.setBillUsed(billPkg.getUsedTrafficB());
                                                        trafficUsedStatus.setBillRemained(billPkg.getRemainTrafficB());
                                                        Log.i(MiuiTrafficCorrection.TAG, billPkg.toString());
                                                    }
                                                    DataUsage.PackageDetail callTimePkg = result.getCallTimePkg();
                                                    if (callTimePkg != null) {
                                                        trafficUsedStatus.setReturnCode(0);
                                                        trafficUsedStatus.setCallTimeEnabled(true);
                                                        trafficUsedStatus.setCallTimeTotal(callTimePkg.getTotalTrafficB());
                                                        trafficUsedStatus.setCallTimeUsed(callTimePkg.getUsedTrafficB());
                                                        trafficUsedStatus.setCallTimeRemained(callTimePkg.getRemainTrafficB());
                                                        Log.i(MiuiTrafficCorrection.TAG, callTimePkg.toString());
                                                    }
                                                    SmsReceiver.this.mListener.onTrafficCorrected(trafficUsedStatus);
                                                } else if (z) {
                                                    return null;
                                                } else {
                                                    TrafficUsedStatus trafficUsedStatus2 = new TrafficUsedStatus(4);
                                                    trafficUsedStatus2.setFailureSms(str);
                                                    SmsReceiver.this.mListener.onTrafficCorrected(trafficUsedStatus2);
                                                }
                                                return null;
                                            }
                                        }.execute(new Void[0]);
                                        return;
                                    }
                                }
                                if (z) {
                                    Log.i(MiuiTrafficCorrection.TAG, String.format("LRL onTrafficCorrected failed, however still wait for SMS number :%d", new Object[]{Integer.valueOf(this.mInterceptedSmsCount)}));
                                    return;
                                } else {
                                    this.mListener.onTrafficCorrected(new TrafficUsedStatus(2));
                                    return;
                                }
                            } else {
                                return;
                            }
                        }
                    }
                    this.mListener.onTrafficCorrected(new TrafficUsedStatus(2));
                }
            } catch (NullPointerException e) {
                Log.i(MiuiTrafficCorrection.TAG, "parse Sms failure", e);
                this.mListener.onTrafficCorrected(new TrafficUsedStatus(4));
            }
        }
    }

    private static class TimeOutTask extends TimerTask {
        private ITrafficCorrection.TrafficCorrectionListener mListener;

        public TimeOutTask(ITrafficCorrection.TrafficCorrectionListener trafficCorrectionListener) {
            this.mListener = trafficCorrectionListener;
        }

        public void run() {
            this.mListener.onTrafficCorrected(new TrafficUsedStatus(3));
        }
    }

    private MiuiTrafficCorrection(Context context) {
        this.mContext = context.getApplicationContext();
        this.mTcManager = TcManager.getInstance();
        this.mPowerManager = (PowerManager) this.mContext.getSystemService("power");
        initTcLib(context);
    }

    private void acquireWakeup() {
        if (this.mWakeLock == null) {
            this.mWakeLock = this.mPowerManager.newWakeLock(1, TAG);
        }
        Log.i(TAG, "LRL acquireWakeup");
        this.mWakeLock.acquire();
    }

    private void addTcDirectionCache() {
        int i = 0;
        for (TcDirection next : this.mDirectionCache) {
            this.mAllDirection.remove(next);
            if (next.getCmdType() == 1) {
                this.mAllDirection.add(i, next);
                i++;
            } else {
                this.mAllDirection.add(i, next);
            }
        }
    }

    private void broadcastTrafficCorrected(TrafficUsedStatus trafficUsedStatus) {
        trafficUsedStatus.setEngine(MiuiTrafficCorrection.class.getSimpleName());
        synchronized (this.mListeners) {
            Iterator<ITrafficCorrection.TrafficCorrectionListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                trafficUsedStatus.setSlotNum(this.mSlotNum);
                it.next().onTrafficCorrected(trafficUsedStatus);
            }
        }
    }

    private ArrayList<TcDirection> getCustomizedInstructions(Map<String, String> map) {
        int indexOf;
        ArrayList<TcDirection> arrayList = new ArrayList<>();
        if (map != null && map.size() > 0) {
            for (Map.Entry next : map.entrySet()) {
                String str = (String) next.getKey();
                if (!TextUtils.isEmpty(str) && (indexOf = str.indexOf("#")) > 0) {
                    str = str.substring(0, indexOf);
                }
                String str2 = (String) next.getValue();
                if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
                    arrayList.add(new TcDirection(str, str2, 1));
                    this.mTcManager.addBlockNumber(str, this.mSlotNum);
                }
            }
        }
        return arrayList;
    }

    public static synchronized MiuiTrafficCorrection getInstance(Context context, String str, int i) {
        synchronized (MiuiTrafficCorrection.class) {
            if (!TextUtils.isEmpty(str)) {
                if (sInstanceMap == null) {
                    sInstanceMap = new HashMap<>();
                }
                MiuiTrafficCorrection miuiTrafficCorrection = sInstanceMap.get(str);
                if (miuiTrafficCorrection == null) {
                    miuiTrafficCorrection = new MiuiTrafficCorrection(context);
                    sInstanceMap.put(str, miuiTrafficCorrection);
                }
                miuiTrafficCorrection.setImsi(str, i);
                return miuiTrafficCorrection;
            }
            if (sInstance == null) {
                String subscriberId = TelephonyUtil.getSubscriberId(context, i);
                sInstance = new MiuiTrafficCorrection(context);
                sInstance.setImsi(subscriberId, i);
            }
            MiuiTrafficCorrection miuiTrafficCorrection2 = sInstance;
            return miuiTrafficCorrection2;
        }
    }

    private List<TcDirection> getInstructionsByTcType(int i) {
        return this.mTcManager.getInstructionsByTcType(this.mSlotNum, i);
    }

    private Set<TcDirection> getTcDirectionCache() {
        SimUserInfo instance = SimUserInfo.getInstance(this.mContext, this.mImsi);
        HashSet hashSet = new HashSet();
        try {
            String lastTrafficTcDirection = instance.getLastTrafficTcDirection();
            if (!TextUtils.isEmpty(lastTrafficTcDirection)) {
                hashSet.add(new TcDirection(lastTrafficTcDirection));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            String lastBillTcDirection = instance.getLastBillTcDirection();
            if (!TextUtils.isEmpty(lastBillTcDirection)) {
                hashSet.add(new TcDirection(lastBillTcDirection));
            }
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        return hashSet;
    }

    private void handleJustRemainStatus(TrafficUsedStatus trafficUsedStatus) {
        if (isTrafficCmdType(this.mCurrentTcType) && trafficUsedStatus.getReturnCode() == 0 && trafficUsedStatus.getUsedTrafficB() < 0) {
            long remainTrafficB = this.mTotalLimit - trafficUsedStatus.getRemainTrafficB();
            if (trafficUsedStatus.isExtraEnable() && trafficUsedStatus.getUsedTrafficB() < 0) {
                remainTrafficB -= trafficUsedStatus.getExtraRemainB();
                trafficUsedStatus.setExtraEnable(false);
            }
            if (remainTrafficB >= 0) {
                trafficUsedStatus.setUsedTrafficB(remainTrafficB);
            } else {
                trafficUsedStatus.setTotalLimitError(true);
                trafficUsedStatus.setUsedTrafficB(0);
            }
            if (trafficUsedStatus.getUsedTrafficB() < 0) {
                trafficUsedStatus.setReturnCode(4);
            }
        }
    }

    private void initTcLib(Context context) {
        this.mTcManager.init(context, context.getPackageName(), "A2FscFVdX1+ULfEz/TTPQVNRXE+lzSe2");
    }

    private boolean isTrafficCmdType(int i) {
        return (i & 1) != 0;
    }

    private void registerSmsSendReceiver() {
        this.mSmsSendRegister = true;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.System.SMS_RECEIVER_ACTION);
        this.mContext.registerReceiver(this.mSmsSentReceiver, intentFilter);
    }

    private void releaseWakeup() {
        PowerManager.WakeLock wakeLock = this.mWakeLock;
        if (wakeLock != null && wakeLock.isHeld()) {
            this.mWakeLock.release();
            this.mWakeLock = null;
            Log.i(TAG, "LRL releaseWakeup");
        }
    }

    private void sendTextMessage(String str, String str2, int i) {
        Log.i(TAG, String.format("addr:%s, text:%s, slotId:%d", new Object[]{str, str2, Integer.valueOf(i)}));
        Intent intent = new Intent();
        intent.setAction(Constants.System.SMS_RECEIVER_ACTION);
        TelephonyUtil.sendTextMessage(str, (String) null, str2, PendingIntent.getBroadcast(this.mContext, 0, intent, 0), (PendingIntent) null, i);
    }

    private synchronized void setConfigUpdated(boolean z) {
        this.mIsUpdated = z;
    }

    private synchronized void setFinished(boolean z) {
        this.mIsFinished = z;
    }

    private void setImsi(String str, int i) {
        this.mTcManager.setImsi(str, i);
        this.mSlotNum = i;
        this.mImsi = str;
        this.mDirectionCache = getTcDirectionCache();
    }

    private boolean setLastTcDirection(TcDirection tcDirection) {
        SimUserInfo instance = SimUserInfo.getInstance(this.mContext, this.mImsi);
        if (tcDirection.getCmdType() == 1) {
            return instance.setLastTrafficTcDirection(tcDirection.toJSON().toString());
        }
        if (tcDirection.getCmdType() == 2) {
            return instance.setLastBillTcDirection(tcDirection.toJSON().toString());
        }
        return false;
    }

    private void startCorrectionByIndex(int i, int i2) {
        boolean z;
        registerSmsSendReceiver();
        String str = null;
        while (true) {
            if (i >= this.mDirectionSize) {
                z = false;
                break;
            }
            this.mCurrentTcDirection = this.mAllDirection.get(i);
            String sendNumber = this.mCurrentTcDirection.getSendNumber();
            String direction = this.mCurrentTcDirection.getDirection();
            this.mCurrentTcType = this.mCurrentTcDirection.getCmdType();
            String receiveNumber = this.mCurrentTcDirection.getReceiveNumber();
            if (TextUtils.isEmpty(receiveNumber)) {
                receiveNumber = sendNumber;
            }
            Log.i(TAG, String.format("i:%d,address:%s,receive:%s,instruction:%s,type:%d", new Object[]{Integer.valueOf(i), sendNumber, receiveNumber, direction, Integer.valueOf(this.mCurrentTcType)}));
            if ((this.mCurrentTcType & i2) != 0) {
                sendTextMessage(sendNumber, direction, this.mSlotNum);
                z = true;
                str = receiveNumber;
                break;
            }
            i++;
            str = receiveNumber;
        }
        if (i < this.mDirectionSize || z) {
            synchronized (this.mSmsFilterLock) {
                if (this.mSmsFilter == null) {
                    this.mSmsFilter = new SmsFilter(this.mContext, this);
                }
                this.mSmsFilter.regist(this.mSlotNum, str, 1, this.mCurrentTcType);
            }
            if (!this.mIsBackground) {
                setFinished(false);
                synchronized (this.mTimerLock) {
                    if (this.mTimer != null) {
                        this.mTimer.cancel();
                    }
                    this.mTimer = new Timer();
                    this.mTimer.schedule(new TimeOutTask(this), ITrafficCorrection.TIMEOUT_MILLION);
                }
                acquireWakeup();
                return;
            }
            return;
        }
        broadcastTrafficCorrected(new TrafficUsedStatus(5));
    }

    private void stopCurrentCorrection() {
        synchronized (this.mSmsFilterLock) {
            if (this.mSmsFilter != null) {
                this.mSmsFilter.unregist();
                this.mSmsFilter = null;
            }
        }
        synchronized (this.mTimerLock) {
            if (this.mTimer != null) {
                this.mTimer.cancel();
                this.mTimer = null;
            }
        }
        releaseWakeup();
        unRegisterSmsSendReceiver();
        setFinished(true);
    }

    private void unRegisterSmsSendReceiver() {
        try {
            if (this.mSmsSendRegister) {
                this.mContext.unregisterReceiver(this.mSmsSentReceiver);
                this.mSmsSendRegister = false;
            }
        } catch (IllegalArgumentException e) {
            Log.i(TAG, "unRegisterSmsSendReceiver", e);
        }
    }

    private void updatePluginLib() {
        this.mTcManager.setImsi(this.mImsi, this.mSlotNum);
    }

    public Map<String, String> getBrands(String str) {
        return this.mTcManager.getBrands(str);
    }

    public Map<Integer, String> getCities(int i) {
        return this.mTcManager.getCities(i);
    }

    public ITrafficCorrection.TrafficConfig getConfig() {
        return null;
    }

    public Map<String, String> getInstructions(int i) {
        return this.mTcManager.getInstructionsMapByType(this.mSlotNum, i);
    }

    public Map<String, String> getOperators() {
        return this.mTcManager.getOperators();
    }

    public int getProvinceCodeByCityCode(int i) {
        return this.mTcManager.getProvinceCodeByCityCode(i);
    }

    public Map<Integer, String> getProvinces() {
        return this.mTcManager.getProvinces();
    }

    public int getTcType() {
        return this.mCurrentTcType;
    }

    public synchronized boolean isConfigUpdated() {
        return this.mIsUpdated;
    }

    public synchronized boolean isFinished() {
        return this.mIsFinished;
    }

    public void onTrafficCorrected(TrafficUsedStatus trafficUsedStatus) {
        Log.i(TAG, "LRL status : " + trafficUsedStatus.toString());
        handleJustRemainStatus(trafficUsedStatus);
        stopCurrentCorrection();
        trafficUsedStatus.setCorrectionType(this.mCurrentTcType);
        broadcastTrafficCorrected(trafficUsedStatus);
        this.mCurrentTcIndex++;
        if (this.mCurrentTcIndex < this.mDirectionSize) {
            if (trafficUsedStatus.getReturnCode() == 0) {
                this.mDirectionType ^= this.mCurrentTcType;
                this.mDirectionCache.add(this.mCurrentTcDirection);
                setLastTcDirection(this.mCurrentTcDirection);
            }
            int i = this.mDirectionType;
            if (i != 0 && i != 4) {
                startCorrectionByIndex(this.mCurrentTcIndex, i);
            }
        }
    }

    public void registerLisener(ITrafficCorrection.TrafficCorrectionListener trafficCorrectionListener) {
        synchronized (this.mListeners) {
            if (trafficCorrectionListener != null) {
                if (!this.mListeners.contains(trafficCorrectionListener)) {
                    this.mListeners.add(trafficCorrectionListener);
                }
            }
        }
    }

    public boolean saveConfig(ITrafficCorrection.TrafficConfig trafficConfig) {
        setConfigUpdated(false);
        stopCurrentCorrection();
        UserConfig userConfig = new UserConfig();
        userConfig.setProvince(trafficConfig.getProvinceId());
        userConfig.setCity(trafficConfig.getCityId());
        userConfig.setOperator(trafficConfig.getOperatorId());
        TcManager.ReturnCode returnCode = TcManager.ReturnCode.Error;
        if (f.j(this.mContext)) {
            updatePluginLib();
            returnCode = this.mTcManager.setConfig(userConfig, this.mSlotNum);
        }
        setConfigUpdated(true);
        broadcastTrafficCorrected(new TrafficUsedStatus(11));
        return returnCode == TcManager.ReturnCode.OK;
    }

    public void setTotalLimit(long j) {
        this.mTotalLimit = j;
    }

    public boolean startCorrection(boolean z, Map<String, String> map) {
        return startCorrection(z, map, 0, 0);
    }

    public boolean startCorrection(boolean z, Map<String, String> map, long j, int i) {
        if (!isFinished()) {
            return false;
        }
        Log.i(TAG, String.format("mina lrl startCorrection, isBackground:%s, type:%s", new Object[]{String.valueOf(z), String.valueOf(i)}));
        this.mCurrentTcIndex = 0;
        this.mIsBackground = z;
        this.mDirectionType = i;
        this.mAllDirection = getInstructionsByTcType(i);
        addTcDirectionCache();
        ArrayList<TcDirection> customizedInstructions = getCustomizedInstructions(map);
        if (!customizedInstructions.isEmpty()) {
            Iterator<TcDirection> it = this.mAllDirection.iterator();
            while (it.hasNext()) {
                if (it.next().getCmdType() == 1) {
                    it.remove();
                }
            }
            Iterator<TcDirection> it2 = customizedInstructions.iterator();
            while (it2.hasNext()) {
                this.mAllDirection.add(0, it2.next());
            }
        }
        this.mDirectionSize = this.mAllDirection.size();
        if (this.mDirectionSize > 0) {
            startCorrectionByIndex(this.mCurrentTcIndex, i);
        } else {
            Log.i(TAG, "instructions is null");
            broadcastTrafficCorrected(new TrafficUsedStatus(5));
        }
        return true;
    }

    public void unRegisterLisener(ITrafficCorrection.TrafficCorrectionListener trafficCorrectionListener) {
        synchronized (this.mListeners) {
            if (trafficCorrectionListener != null) {
                if (this.mListeners.contains(trafficCorrectionListener)) {
                    this.mListeners.remove(trafficCorrectionListener);
                }
            }
        }
    }

    public boolean updateSMSTemplate(String str, String str2, String str3) {
        setConfigUpdated(false);
        stopCurrentCorrection();
        UserConfig userConfig = new UserConfig(str, str2, str3);
        TcManager.ReturnCode returnCode = TcManager.ReturnCode.Error;
        if (f.j(this.mContext)) {
            updatePluginLib();
            returnCode = this.mTcManager.setConfig(userConfig, this.mSlotNum, 7);
        }
        setConfigUpdated(true);
        broadcastTrafficCorrected(new TrafficUsedStatus(11));
        return returnCode == TcManager.ReturnCode.OK;
    }
}
