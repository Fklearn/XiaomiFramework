package com.miui.networkassistant.traffic.correction;

import android.content.Context;
import b.b.c.h.f;
import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.dual.SimCardHelper;
import com.miui.networkassistant.traffic.correction.webcorrection.CmccCorrection;
import com.miui.networkassistant.traffic.correction.webcorrection.MiCorrection;

public class WebCorrectionManager {
    private static final String TAG = "WebCorrectionManager";
    private static WebCorrectionManager sInstance;
    private Context mContext;

    private WebCorrectionManager(Context context) {
        this.mContext = context;
    }

    public static synchronized WebCorrectionManager getInstance(Context context) {
        WebCorrectionManager webCorrectionManager;
        synchronized (WebCorrectionManager.class) {
            if (sInstance == null) {
                sInstance = new WebCorrectionManager(context);
            }
            webCorrectionManager = sInstance;
        }
        return webCorrectionManager;
    }

    public IWebCorrection getWebCorrection(String str) {
        if (isMiWebCorrectSupported(str)) {
            return MiCorrection.getInstance(this.mContext);
        }
        if (isCmccWebCorrectSupported(str)) {
            return CmccCorrection.getInstance(this.mContext);
        }
        return null;
    }

    public boolean isCmccWebCorrectSupported(String str) {
        SimCardHelper instance = SimCardHelper.getInstance(this.mContext);
        return f.i(this.mContext) && instance.getSlotNumByImsi(str) == instance.getCurrentMobileSlotNum() && SimUserInfo.getInstance(this.mContext, str).isSupportCmccWebCorrection();
    }

    public boolean isMiWebCorrectSupported(String str) {
        SimUserInfo instance = SimUserInfo.getInstance(this.mContext, str);
        return instance.getWebCorrectionStatusRefreshTime() <= System.currentTimeMillis() || instance.isWebCorrectionSupported();
    }

    public boolean isServiceSupported(String str) {
        return isCmccWebCorrectSupported(str) || isMiWebCorrectSupported(str);
    }
}
