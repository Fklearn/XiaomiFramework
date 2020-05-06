package com.miui.networkassistant.traffic.correction.webcorrection;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import b.b.c.g.a;
import b.b.c.h.j;
import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.dual.SimCardHelper;
import com.miui.networkassistant.model.TrafficUsedStatus;
import com.miui.networkassistant.traffic.correction.ITrafficCorrection;
import com.miui.networkassistant.traffic.correction.IWebCorrection;
import com.miui.networkassistant.utils.AnalyticsHelper;
import com.miui.networkassistant.utils.FormatBytesUtil;
import com.miui.securitycenter.R;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import miui.provider.ExtraNetwork;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CmccCorrection implements IWebCorrection {
    private static final String AUTO_LOGIN_URL = "http://wx.10086.cn/website/bind/oneKeySignIn/login?checkCtrol=1";
    private static final int BACKGROUND_CORRECTION_FAILED = 0;
    private static final int BACKGROUND_CORRECTION_SUCCESS = 1;
    private static final String CMCC_URL = "http://wx.10086.cn/website/personalHome/new/index?secondChannel=60018";
    private static boolean DEBUG = false;
    private static final String GET_ACCESS_TOKEN_URL = "http://wx.10086.cn/website/bind/oneKeySignIn/getRzAccessToken?timeStr=";
    private static final String GET_BALANCE_INFO_URL = "http://wx.10086.cn/website/personalHome/fareBalance";
    private static final String GET_MOBILE_NUMBER_URL = "http://wx.10086.cn/website/bind/oneKeySignIn/getMobileNumber";
    private static final String GET_MOBILE_R_URL = "http://www.cmpassport.com/openapi/getMobileR?ver=1.0&appId=000023&msgId=%1$s&timestamp=%2$s&accessToken=%3$s&openType=1&message=%4$s&redirectUrl=";
    private static final String GET_TRAFFIC_INFO_URL = "http://wx.10086.cn/website/personalHome/planRemain";
    private static final int MANUAL_CORRECTION_FAILED = 2;
    private static final int MANUAL_CORRECTION_SUCCESS = 3;
    private static final String TAG = "CmccCorrection";
    private static CmccCorrection sInstance;
    private AsyncTask<String, Void, String> mAsyncTask;
    private Context mContext;
    private boolean mIsBackground;
    private int mSlotNum;
    private STATUS mStatus = STATUS.STATUS_GET_ACCESS_TOKEN;
    private TrafficUsedStatus mTrafficUsedStatus;
    private ITrafficCorrection.TrafficCorrectionListener mWebCorrectionListener;

    /* renamed from: com.miui.networkassistant.traffic.correction.webcorrection.CmccCorrection$2  reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$networkassistant$traffic$correction$webcorrection$CmccCorrection$STATUS = new int[STATUS.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(12:0|1|2|3|4|5|6|7|8|9|10|(3:11|12|14)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x0040 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001f */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x002a */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0035 */
        static {
            /*
                com.miui.networkassistant.traffic.correction.webcorrection.CmccCorrection$STATUS[] r0 = com.miui.networkassistant.traffic.correction.webcorrection.CmccCorrection.STATUS.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$miui$networkassistant$traffic$correction$webcorrection$CmccCorrection$STATUS = r0
                int[] r0 = $SwitchMap$com$miui$networkassistant$traffic$correction$webcorrection$CmccCorrection$STATUS     // Catch:{ NoSuchFieldError -> 0x0014 }
                com.miui.networkassistant.traffic.correction.webcorrection.CmccCorrection$STATUS r1 = com.miui.networkassistant.traffic.correction.webcorrection.CmccCorrection.STATUS.STATUS_ERROR     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                int[] r0 = $SwitchMap$com$miui$networkassistant$traffic$correction$webcorrection$CmccCorrection$STATUS     // Catch:{ NoSuchFieldError -> 0x001f }
                com.miui.networkassistant.traffic.correction.webcorrection.CmccCorrection$STATUS r1 = com.miui.networkassistant.traffic.correction.webcorrection.CmccCorrection.STATUS.STATUS_GET_ACCESS_TOKEN     // Catch:{ NoSuchFieldError -> 0x001f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                int[] r0 = $SwitchMap$com$miui$networkassistant$traffic$correction$webcorrection$CmccCorrection$STATUS     // Catch:{ NoSuchFieldError -> 0x002a }
                com.miui.networkassistant.traffic.correction.webcorrection.CmccCorrection$STATUS r1 = com.miui.networkassistant.traffic.correction.webcorrection.CmccCorrection.STATUS.STATUS_GET_MOBILE_NUMBER     // Catch:{ NoSuchFieldError -> 0x002a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x002a }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x002a }
            L_0x002a:
                int[] r0 = $SwitchMap$com$miui$networkassistant$traffic$correction$webcorrection$CmccCorrection$STATUS     // Catch:{ NoSuchFieldError -> 0x0035 }
                com.miui.networkassistant.traffic.correction.webcorrection.CmccCorrection$STATUS r1 = com.miui.networkassistant.traffic.correction.webcorrection.CmccCorrection.STATUS.STATUS_LOGIN     // Catch:{ NoSuchFieldError -> 0x0035 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0035 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0035 }
            L_0x0035:
                int[] r0 = $SwitchMap$com$miui$networkassistant$traffic$correction$webcorrection$CmccCorrection$STATUS     // Catch:{ NoSuchFieldError -> 0x0040 }
                com.miui.networkassistant.traffic.correction.webcorrection.CmccCorrection$STATUS r1 = com.miui.networkassistant.traffic.correction.webcorrection.CmccCorrection.STATUS.STATUS_GET_TRAFFIC_INFO     // Catch:{ NoSuchFieldError -> 0x0040 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0040 }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0040 }
            L_0x0040:
                int[] r0 = $SwitchMap$com$miui$networkassistant$traffic$correction$webcorrection$CmccCorrection$STATUS     // Catch:{ NoSuchFieldError -> 0x004b }
                com.miui.networkassistant.traffic.correction.webcorrection.CmccCorrection$STATUS r1 = com.miui.networkassistant.traffic.correction.webcorrection.CmccCorrection.STATUS.STATUS_GET_BALANCE_INFO     // Catch:{ NoSuchFieldError -> 0x004b }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x004b }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x004b }
            L_0x004b:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.traffic.correction.webcorrection.CmccCorrection.AnonymousClass2.<clinit>():void");
        }
    }

    private enum STATUS {
        STATUS_ERROR,
        STATUS_GET_ACCESS_TOKEN,
        STATUS_GET_MOBILE_NUMBER,
        STATUS_LOGIN,
        STATUS_GET_TRAFFIC_INFO,
        STATUS_GET_BALANCE_INFO,
        STATUS_FINISH
    }

    private CmccCorrection(Context context) {
        this.mContext = context;
        CookieHandler.setDefault(new CookieManager((CookieStore) null, CookiePolicy.ACCEPT_ALL));
    }

    private void checkPackageItem(JSONObject jSONObject, TrafficUsedStatus trafficUsedStatus) {
        if (jSONObject != null) {
            try {
                JSONObject jSONObject2 = jSONObject.getJSONArray("resourcesLeftInfo").getJSONObject(0);
                if (!isValidPackage(jSONObject.getString("secResourcesName"))) {
                    String string = jSONObject2.getString("totalRes");
                    String string2 = jSONObject2.getString("usedRes");
                    String string3 = jSONObject2.getString("remainRes");
                    String string4 = jSONObject2.getString("unit");
                    int i = 1;
                    if ("03".equals(string4)) {
                        i = 1024;
                    } else if ("05".equals(string4)) {
                        i = 1073741824;
                    }
                    long j = (long) i;
                    long parseLong = Long.parseLong(string) * j;
                    long parseLong2 = Long.parseLong(string2) * j;
                    long parseLong3 = Long.parseLong(string3) * j;
                    trafficUsedStatus.setTotalTrafficB(trafficUsedStatus.getTotalTrafficB() - parseLong);
                    trafficUsedStatus.setUsedTrafficB(trafficUsedStatus.getUsedTrafficB() - parseLong2);
                    trafficUsedStatus.setRemainTrafficB(trafficUsedStatus.getRemainTrafficB() - parseLong3);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NumberFormatException e2) {
                e2.printStackTrace();
            }
        }
    }

    private void filterTrafficResult(String str, TrafficUsedStatus trafficUsedStatus) {
        try {
            JSONArray jSONArray = new JSONObject(str).getJSONArray("planRemain");
            JSONObject jSONObject = null;
            int i = 0;
            while (true) {
                if (i >= jSONArray.length()) {
                    break;
                }
                JSONObject jSONObject2 = jSONArray.getJSONObject(i);
                if ("04".equals(jSONObject2.optString("resourcesCode", ""))) {
                    jSONObject = jSONObject2;
                    break;
                }
                i++;
            }
            if (jSONObject != null) {
                JSONArray jSONArray2 = jSONObject.getJSONArray("secResourcesInfo");
                for (int i2 = 0; i2 < jSONArray2.length(); i2++) {
                    checkPackageItem(jSONArray2.getJSONObject(i2), trafficUsedStatus);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void finishCorrection() {
        ITrafficCorrection.TrafficCorrectionListener trafficCorrectionListener = this.mWebCorrectionListener;
        if (trafficCorrectionListener != null) {
            trafficCorrectionListener.onTrafficCorrected(this.mTrafficUsedStatus);
        }
        AnalyticsHelper.trackCmccWebCorrect(this.mIsBackground ? 1 : 3);
    }

    public static synchronized CmccCorrection getInstance(Context context) {
        CmccCorrection cmccCorrection;
        synchronized (CmccCorrection.class) {
            if (sInstance == null) {
                sInstance = new CmccCorrection(context);
            }
            cmccCorrection = sInstance;
        }
        return cmccCorrection;
    }

    private void handleStateGetAccess(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (jSONObject.getInt("resultCode") == 0) {
                String str2 = String.format(GET_MOBILE_R_URL, new Object[]{jSONObject.getString("msgId"), new SimpleDateFormat("yyyyMMddhhmmssSSS").format(new Date()), jSONObject.getString("accessToken"), jSONObject.getString("message")}) + URLEncoder.encode(GET_MOBILE_NUMBER_URL);
                if (DEBUG) {
                    Log.d(TAG, "get mobile num:" + str2);
                }
                this.mStatus = STATUS.STATUS_GET_MOBILE_NUMBER;
                httpGet(str2);
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        notifyCorrectionFailure();
        if (DEBUG) {
            Log.d(TAG, "Fail to get access token:" + str);
        }
    }

    private void handleStateGetBalanceInfo(String str) {
        this.mStatus = STATUS.STATUS_FINISH;
        if (DEBUG) {
            String str2 = TAG;
            Log.d(str2, "STATUS_GET_BALANCE_INFO:" + str);
        }
        parseBalanceResult(str, this.mTrafficUsedStatus);
    }

    private void handleStateGetMobileNum(String str) {
        if (DEBUG) {
            String str2 = TAG;
            Log.d(str2, "STATUS_GET_MOBILE_NUMBER:" + str);
        }
        this.mStatus = STATUS.STATUS_LOGIN;
        httpGet(AUTO_LOGIN_URL);
    }

    private void handleStateGetTrafficInfo(String str) {
        if (DEBUG) {
            String str2 = TAG;
            Log.d(str2, "STATUS_GET_TRAFFIC_INFO:" + str);
        }
        parseTrafficResult(str, this.mTrafficUsedStatus);
        this.mStatus = STATUS.STATUS_GET_BALANCE_INFO;
        httpGet(GET_BALANCE_INFO_URL);
    }

    private void handleStateLogin(String str) {
        try {
            if (new JSONObject(str).getInt("status") == 0) {
                this.mStatus = STATUS.STATUS_GET_TRAFFIC_INFO;
                httpGet(GET_TRAFFIC_INFO_URL);
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        notifyCorrectionFailure();
        if (DEBUG) {
            String str2 = TAG;
            Log.d(str2, "Fail to login:" + str);
        }
    }

    private void httpGet(String str) {
        this.mAsyncTask = new AsyncTask<String, Void, String>() {
            /* access modifiers changed from: protected */
            public String doInBackground(String... strArr) {
                return a.a(strArr[0], new j("networkassistant_cmcccorrection"));
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(String str) {
                CmccCorrection.this.processStatus(str);
            }
        };
        this.mAsyncTask.execute(new String[]{str});
    }

    private boolean isValidPackage(String str) {
        return true;
    }

    private void notifyCorrectionFailure() {
        this.mStatus = STATUS.STATUS_ERROR;
        if (this.mWebCorrectionListener != null) {
            this.mWebCorrectionListener.onTrafficCorrected(new TrafficUsedStatus(6, this.mSlotNum));
            AnalyticsHelper.trackCmccWebCorrect(this.mIsBackground ? 0 : 2);
        }
    }

    private void parseBalanceResult(String str, TrafficUsedStatus trafficUsedStatus) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            trafficUsedStatus.setFromWeb(true);
            if (jSONObject.has("remainTelFee")) {
                trafficUsedStatus.setBillEnabled(true);
                trafficUsedStatus.setBillRemained((long) (jSONObject.optDouble("remainTelFee", 0.0d) * 100.0d));
                trafficUsedStatus.setReturnCode(0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseTrafficResult(String str, TrafficUsedStatus trafficUsedStatus) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            trafficUsedStatus.setFromWeb(true);
            if (jSONObject.has("totalIsuse")) {
                float optDouble = (float) jSONObject.optDouble("totalIsuse");
                String optString = jSONObject.optString("unit");
                long bytesByUnit = FormatBytesUtil.getBytesByUnit(optDouble, optString);
                long bytesByUnit2 = FormatBytesUtil.getBytesByUnit((float) jSONObject.optDouble("flowIsuse"), optString);
                trafficUsedStatus.setTotalTrafficB(bytesByUnit);
                trafficUsedStatus.setRemainTrafficB(bytesByUnit2);
                trafficUsedStatus.setUsedTrafficB(bytesByUnit - bytesByUnit2);
                trafficUsedStatus.setNormalStable(true);
                trafficUsedStatus.setReturnCode(0);
                filterTrafficResult(str, trafficUsedStatus);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public void processStatus(String str) {
        switch (AnonymousClass2.$SwitchMap$com$miui$networkassistant$traffic$correction$webcorrection$CmccCorrection$STATUS[this.mStatus.ordinal()]) {
            case 1:
                notifyCorrectionFailure();
                return;
            case 2:
                handleStateGetAccess(str);
                return;
            case 3:
                handleStateGetMobileNum(str);
                return;
            case 4:
                handleStateLogin(str);
                return;
            case 5:
                handleStateGetTrafficInfo(str);
                return;
            case 6:
                handleStateGetBalanceInfo(str);
                finishCorrection();
                return;
            default:
                return;
        }
    }

    private void startCorrection() {
        this.mStatus = STATUS.STATUS_GET_ACCESS_TOKEN;
        String str = GET_ACCESS_TOKEN_URL + System.currentTimeMillis();
        if (DEBUG) {
            Log.d(TAG, "startCorrection:" + str);
        }
        httpGet(str);
    }

    public void queryDataUsage(String str, long j, boolean z, ITrafficCorrection.TrafficCorrectionListener trafficCorrectionListener) {
        Log.i(TAG, "start cmcc correction");
        this.mIsBackground = z;
        this.mSlotNum = SimCardHelper.getInstance(this.mContext).getSlotNumByImsi(str);
        AsyncTask<String, Void, String> asyncTask = this.mAsyncTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
        }
        this.mWebCorrectionListener = trafficCorrectionListener;
        this.mTrafficUsedStatus = new TrafficUsedStatus(6, this.mSlotNum);
        startCorrection();
        if (!z) {
            Context context = this.mContext;
            ExtraNetwork.navigateToRichWebActivity(context, "http://wx.10086.cn/website/personalHome/new/index?secondChannel=60018&slotid=" + SimUserInfo.getInstance(this.mContext, str).getSlotNum(), this.mContext.getResources().getString(R.string.main_traffic_detail), false, this.mContext.getResources().getString(R.string.app_name_na), true);
            AnalyticsHelper.trackShowCmccWebsite();
        }
    }
}
