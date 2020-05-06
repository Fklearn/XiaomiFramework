package com.miui.networkassistant.traffic.correction.webcorrection;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.model.TrafficUsedStatus;
import com.miui.networkassistant.traffic.correction.ITrafficCorrection;
import com.miui.networkassistant.traffic.correction.IWebCorrection;
import com.miui.networkassistant.traffic.correction.WebCorrectionManager;
import com.miui.networkassistant.utils.TelephonyUtil;
import com.miui.networkassistant.webapi.DataUsageResult;
import com.miui.networkassistant.webapi.WebApiAccessHelper;

public class MiCorrection implements IWebCorrection {
    private static final String TAG = "MiCorrection";
    private static MiCorrection sInstance;
    /* access modifiers changed from: private */
    public Context mContext;

    private MiCorrection(Context context) {
        this.mContext = context;
    }

    public static synchronized MiCorrection getInstance(Context context) {
        MiCorrection miCorrection;
        synchronized (MiCorrection.class) {
            if (sInstance == null) {
                sInstance = new MiCorrection(context);
            }
            miCorrection = sInstance;
        }
        return miCorrection;
    }

    /* access modifiers changed from: private */
    public DataUsageResult query(String str, long j) {
        SimUserInfo instance = SimUserInfo.getInstance(this.mContext, str);
        DataUsageResult queryDataUsage = WebApiAccessHelper.queryDataUsage(instance.getImsi(), String.valueOf(instance.getCity()), TelephonyUtil.getPhoneNumber(this.mContext, instance.getSlotNum()), instance.getOperator(), j, instance.getIccid());
        if (queryDataUsage.isSuccess()) {
            if (!instance.isWebCorrectionSupported()) {
                instance.saveWebCorrectionSupported(true);
            }
            return queryDataUsage;
        }
        if (queryDataUsage.isServiceNotSupported()) {
            int oldAge = queryDataUsage.getOldAge();
            instance.saveWebCorrectionSupported(false);
            instance.saveWebCorrectionStatusRefreshTime(System.currentTimeMillis() + (((long) oldAge) * 86400000));
            String str2 = TAG;
            Log.i(str2, "web correction service is not supported current imsi , oldAge: " + oldAge);
        }
        return queryDataUsage;
    }

    public void queryDataUsage(String str, long j, boolean z, ITrafficCorrection.TrafficCorrectionListener trafficCorrectionListener) {
        final String str2 = str;
        final long j2 = j;
        final ITrafficCorrection.TrafficCorrectionListener trafficCorrectionListener2 = trafficCorrectionListener;
        final boolean z2 = z;
        new AsyncTask<Void, Void, DataUsageResult>() {
            /* access modifiers changed from: protected */
            public DataUsageResult doInBackground(Void... voidArr) {
                return MiCorrection.this.query(str2, j2);
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(DataUsageResult dataUsageResult) {
                if (trafficCorrectionListener2 != null) {
                    if (dataUsageResult.isSuccess()) {
                        TrafficUsedStatus trafficUsedStatus = new TrafficUsedStatus(dataUsageResult.getUsedFlow(), dataUsageResult.getLeftFlow());
                        trafficUsedStatus.setTotalTrafficB(dataUsageResult.getTotal());
                        trafficUsedStatus.setNormalStable(true);
                        trafficUsedStatus.setFromWeb(true);
                        if (dataUsageResult.isIdleOn()) {
                            trafficUsedStatus.setLeisureEnable(true);
                            trafficUsedStatus.setLeisureStable(true);
                            trafficUsedStatus.setLeisureTotalB(dataUsageResult.getIdleTotal());
                            trafficUsedStatus.setLeisureUsedB(dataUsageResult.getIdleUsed());
                            trafficUsedStatus.setLeisureRemainB(dataUsageResult.getIdleLeft());
                        }
                        if (dataUsageResult.isBillOn()) {
                            trafficUsedStatus.setBillEnabled(true);
                            trafficUsedStatus.setBillRemained(dataUsageResult.getBillLeft());
                        }
                        if (dataUsageResult.isCallTimeOn()) {
                            trafficUsedStatus.setCallTimeEnabled(true);
                            trafficUsedStatus.setCallTimeTotal(dataUsageResult.getCallTimeTotal());
                            trafficUsedStatus.setCallTimeRemained(dataUsageResult.getCallTimeLeft());
                            trafficUsedStatus.setCallTimeUsed(dataUsageResult.getCallTimeUsed());
                        }
                        trafficCorrectionListener2.onTrafficCorrected(trafficUsedStatus);
                    } else if (WebCorrectionManager.getInstance(MiCorrection.this.mContext).isCmccWebCorrectSupported(str2)) {
                        CmccCorrection.getInstance(MiCorrection.this.mContext).queryDataUsage(str2, j2, z2, trafficCorrectionListener2);
                    } else {
                        trafficCorrectionListener2.onTrafficCorrected(new TrafficUsedStatus(6));
                    }
                }
            }
        }.execute(new Void[0]);
    }
}
