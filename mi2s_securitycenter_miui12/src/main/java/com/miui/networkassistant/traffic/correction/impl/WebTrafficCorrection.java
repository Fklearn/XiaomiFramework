package com.miui.networkassistant.traffic.correction.impl;

import android.content.Context;
import android.util.Log;
import b.b.c.h.f;
import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.dual.SimCardHelper;
import com.miui.networkassistant.model.TrafficUsedStatus;
import com.miui.networkassistant.traffic.correction.ITrafficCorrection;
import com.miui.networkassistant.traffic.correction.IWebCorrection;
import com.miui.networkassistant.traffic.correction.WebCorrectionManager;
import com.miui.networkassistant.utils.AnalyticsHelper;
import com.miui.networkassistant.utils.TelephonyUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WebTrafficCorrection implements ITrafficCorrection {
    private static final String TAG = "WebTrafficCorrection";
    private static HashMap<String, WebTrafficCorrection> sInstanceMap;
    private Context mContext;
    private Object mCorrectionLock = new Object();
    private Map<String, String> mCustomizedSms;
    private String mImsi;
    private boolean mIsFinished = true;
    private ArrayList<ITrafficCorrection.TrafficCorrectionListener> mListeners = new ArrayList<>();
    private int mSlotNum;
    private long mTotalLimit;
    private ITrafficCorrection mTrafficCorrection;
    private WebCorrectionManager mWebCorrectionManager;

    private class WebCorrectListener implements ITrafficCorrection.TrafficCorrectionListener {
        private boolean mIsBackground;
        private int mType;

        public WebCorrectListener(boolean z, int i) {
            this.mIsBackground = z;
            this.mType = i;
        }

        public void onTrafficCorrected(TrafficUsedStatus trafficUsedStatus) {
            WebTrafficCorrection.this.handleJustRemainStatus(trafficUsedStatus, this.mType);
            if (trafficUsedStatus == null || trafficUsedStatus.getReturnCode() != 0) {
                WebTrafficCorrection.this.handleStatusFail(this.mIsBackground, this.mType);
            } else {
                WebTrafficCorrection.this.broadcastTrafficCorrected(trafficUsedStatus);
                WebTrafficCorrection.this.saveAnalytics(true);
                Log.i(WebTrafficCorrection.TAG, trafficUsedStatus.toString());
            }
            WebTrafficCorrection.this.setFinished(true);
        }
    }

    private WebTrafficCorrection(Context context, String str, int i, ITrafficCorrection iTrafficCorrection) {
        this.mContext = context;
        this.mTrafficCorrection = iTrafficCorrection;
        this.mSlotNum = i;
        this.mWebCorrectionManager = WebCorrectionManager.getInstance(this.mContext);
        this.mImsi = str;
    }

    /* access modifiers changed from: private */
    public void broadcastTrafficCorrected(TrafficUsedStatus trafficUsedStatus) {
        synchronized (this.mListeners) {
            Iterator<ITrafficCorrection.TrafficCorrectionListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                trafficUsedStatus.setSlotNum(this.mSlotNum);
                it.next().onTrafficCorrected(trafficUsedStatus);
            }
        }
    }

    public static synchronized WebTrafficCorrection getInstance(Context context, String str, int i, ITrafficCorrection iTrafficCorrection) {
        WebTrafficCorrection webTrafficCorrection;
        synchronized (WebTrafficCorrection.class) {
            if (sInstanceMap == null) {
                sInstanceMap = new HashMap<>();
            }
            webTrafficCorrection = sInstanceMap.get(str);
            if (webTrafficCorrection == null) {
                webTrafficCorrection = new WebTrafficCorrection(context, str, i, iTrafficCorrection);
                sInstanceMap.put(str, webTrafficCorrection);
            }
        }
        return webTrafficCorrection;
    }

    /* access modifiers changed from: private */
    public void handleJustRemainStatus(TrafficUsedStatus trafficUsedStatus, int i) {
        if (isTrafficCmdType(i) && trafficUsedStatus.getReturnCode() == 0 && !trafficUsedStatus.isNormalStable()) {
            long remainTrafficB = this.mTotalLimit - trafficUsedStatus.getRemainTrafficB();
            if (remainTrafficB >= 0) {
                trafficUsedStatus.setUsedTrafficB(remainTrafficB);
                return;
            }
            trafficUsedStatus.setTotalLimitError(true);
            trafficUsedStatus.setUsedTrafficB(0);
        }
    }

    /* access modifiers changed from: private */
    public void handleStatusFail(boolean z, int i) {
        if (TelephonyUtil.isSupportSmsCorrection(SimUserInfo.getInstance(this.mContext, this.mImsi).getOperator())) {
            startSmsCorrection(z, i);
            saveAnalytics(false);
            return;
        }
        broadcastTrafficCorrected(new TrafficUsedStatus(6));
    }

    private boolean isTrafficCmdType(int i) {
        return (i & 1) != 0;
    }

    /* access modifiers changed from: private */
    public void saveAnalytics(boolean z) {
        SimUserInfo instance = SimUserInfo.getInstance(this.mContext, this.mImsi);
        AnalyticsHelper.trackTrafficWebCorrection(String.valueOf(instance.getBrand()), String.valueOf(instance.getProvince()), z);
    }

    /* access modifiers changed from: private */
    public void setFinished(boolean z) {
        synchronized (this.mCorrectionLock) {
            this.mIsFinished = z;
        }
    }

    private boolean startSmsCorrection(boolean z, int i) {
        Log.i(TAG, "startSmsCorrection");
        return this.mTrafficCorrection.startCorrection(z, this.mCustomizedSms, 0, i);
    }

    private void startWebCorrection(boolean z, long j, int i) {
        Log.i(TAG, "startWebCorrection");
        IWebCorrection webCorrection = WebCorrectionManager.getInstance(this.mContext).getWebCorrection(this.mImsi);
        if (webCorrection == null) {
            handleStatusFail(z, i);
            return;
        }
        webCorrection.queryDataUsage(this.mImsi, j, z, new WebCorrectListener(z, i));
    }

    public Map<String, String> getBrands(String str) {
        return this.mTrafficCorrection.getBrands(str);
    }

    public Map<Integer, String> getCities(int i) {
        return this.mTrafficCorrection.getCities(i);
    }

    public ITrafficCorrection.TrafficConfig getConfig() {
        return this.mTrafficCorrection.getConfig();
    }

    public Map<String, String> getInstructions(int i) {
        return this.mTrafficCorrection.getInstructions(i);
    }

    public Map<String, String> getOperators() {
        return this.mTrafficCorrection.getOperators();
    }

    public int getProvinceCodeByCityCode(int i) {
        return this.mTrafficCorrection.getProvinceCodeByCityCode(i);
    }

    public Map<Integer, String> getProvinces() {
        return this.mTrafficCorrection.getProvinces();
    }

    public int getTcType() {
        return this.mTrafficCorrection.getTcType();
    }

    public synchronized boolean isConfigUpdated() {
        return this.mTrafficCorrection.isConfigUpdated();
    }

    public boolean isFinished() {
        boolean z;
        synchronized (this.mCorrectionLock) {
            z = this.mIsFinished && this.mTrafficCorrection.isFinished();
        }
        return z;
    }

    public void registerLisener(ITrafficCorrection.TrafficCorrectionListener trafficCorrectionListener) {
        synchronized (this.mListeners) {
            if (trafficCorrectionListener != null) {
                if (!this.mListeners.contains(trafficCorrectionListener)) {
                    this.mListeners.add(trafficCorrectionListener);
                }
            }
        }
        this.mTrafficCorrection.registerLisener(trafficCorrectionListener);
    }

    public boolean saveConfig(ITrafficCorrection.TrafficConfig trafficConfig) {
        boolean saveConfig = this.mTrafficCorrection.saveConfig(trafficConfig);
        broadcastTrafficCorrected(new TrafficUsedStatus(11));
        return saveConfig;
    }

    public void setTotalLimit(long j) {
        this.mTotalLimit = j;
        this.mTrafficCorrection.setTotalLimit(j);
    }

    public boolean startCorrection(boolean z, long j, int i) {
        boolean z2 = false;
        if (!isFinished()) {
            return false;
        }
        Log.i(TAG, "startCorrection,isBackground:" + String.valueOf(z));
        synchronized (this.mCorrectionLock) {
            if (this.mWebCorrectionManager.isServiceSupported(this.mImsi) && f.j(this.mContext)) {
                startWebCorrection(z, j, i);
                setFinished(false);
                z2 = true;
            } else {
                SimUserInfo instance = SimUserInfo.getInstance(this.mContext, this.mImsi);
                boolean isSupportSmsCorrection = TelephonyUtil.isSupportSmsCorrection(instance.getOperator());
                if (!(!TelephonyUtil.isAirModeOn(this.mContext) && instance.isCorrectionEffective() && SimCardHelper.getInstance(this.mContext).isSimCardReady(instance.getSlotNum())) || !isSupportSmsCorrection) {
                    broadcastTrafficCorrected(new TrafficUsedStatus(6));
                    setFinished(true);
                } else {
                    z2 = startSmsCorrection(z, i);
                }
            }
        }
        return z2;
    }

    public boolean startCorrection(boolean z, Map<String, String> map) {
        return startCorrection(z, map, 0, 0);
    }

    public boolean startCorrection(boolean z, Map<String, String> map, long j, int i) {
        this.mCustomizedSms = map;
        return startCorrection(z, j, i);
    }

    public void unRegisterLisener(ITrafficCorrection.TrafficCorrectionListener trafficCorrectionListener) {
        synchronized (this.mListeners) {
            if (trafficCorrectionListener != null) {
                if (this.mListeners.contains(trafficCorrectionListener)) {
                    this.mListeners.remove(trafficCorrectionListener);
                }
            }
        }
        this.mTrafficCorrection.unRegisterLisener(trafficCorrectionListener);
    }

    public boolean updateSMSTemplate(String str, String str2, String str3) {
        return this.mTrafficCorrection.updateSMSTemplate(str, str2, str3);
    }
}
