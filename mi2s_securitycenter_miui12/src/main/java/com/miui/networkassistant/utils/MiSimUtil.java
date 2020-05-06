package com.miui.networkassistant.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import b.b.o.g.e;
import com.miui.networkassistant.config.CommonConfig;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import org.json.JSONArray;
import org.json.JSONObject;

public class MiSimUtil {
    private static final String TAG = "MiSimUtil";
    private static final int TYPE_VIRTUAL_SIM = 1;

    private MiSimUtil() {
    }

    public static String getMiSimActiveBtnTxt(Context context, String str) {
        return parseMiSimCloudDataStr(context, "btn_txt", str);
    }

    public static boolean isMiSimCloudEnable(Context context) {
        String miSimCloudData = CommonConfig.getInstance(context).getMiSimCloudData();
        if (TextUtils.isEmpty(miSimCloudData)) {
            return false;
        }
        try {
            return new JSONObject(miSimCloudData).optBoolean("enable");
        } catch (Exception e) {
            Log.i(TAG, "getMiSimCloudEnable", e);
            return false;
        }
    }

    public static boolean isMiSimEnable(Context context, int i) {
        return isSupportGlobalVirtualSim(context) && TelephonyUtil.isVirtualSim(context, i);
    }

    private static boolean isShowGlobalVirtualSim(Context context) {
        int currentMobileSlotNum = TelephonyUtil.getCurrentMobileSlotNum();
        return TelephonyUtil.isChinaOperator(currentMobileSlotNum) && TelephonyUtil.isNetworkRoaming(context, currentMobileSlotNum);
    }

    public static boolean isSupportGlobalVirtualSim(Context context) {
        try {
            return ((Boolean) e.a(Class.forName("android.util.VirtualSim"), "isSupportMiSim", (Class<?>[]) new Class[]{Context.class}, context)).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String parseMiSimCloudDataStr(Context context, String str, String str2) {
        String miSimCloudData = CommonConfig.getInstance(context).getMiSimCloudData();
        if (!TextUtils.isEmpty(miSimCloudData)) {
            try {
                JSONObject jSONObject = new JSONObject(miSimCloudData);
                int optInt = jSONObject.optInt("version");
                if (isShowGlobalVirtualSim(context) && optInt == 2) {
                    JSONArray optJSONArray = jSONObject.optJSONArray("extra");
                    for (int i = 0; i < optJSONArray.length(); i++) {
                        JSONObject optJSONObject = optJSONArray.optJSONObject(i);
                        if (optJSONObject != null && optJSONObject.optInt("type") == 1) {
                            return optJSONObject.optString(str);
                        }
                    }
                }
                return jSONObject.optString(str, str2);
            } catch (Exception e) {
                Log.i(TAG, "parseMiSimCloudDataStr", e);
            }
        }
        return str2;
    }

    public static void startMiSimMainActivity(Context context, String str) {
        try {
            Intent parseUri = Intent.parseUri(parseMiSimCloudDataStr(context, MijiaAlertModel.KEY_URL, str), 1);
            parseUri.setFlags(268468224);
            context.startActivity(parseUri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
