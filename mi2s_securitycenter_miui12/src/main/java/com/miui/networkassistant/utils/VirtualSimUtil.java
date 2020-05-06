package com.miui.networkassistant.utils;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.miui.networkassistant.model.VirtualNotiInfo;
import com.miui.networkassistant.model.VirtualSimInfo;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.support.provider.f;
import com.xiaomi.stat.MiStat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class VirtualSimUtil {
    public static final String ACTION_DETAIL_PAGE = "assistInfo";
    public static final String ACTION_NOTIFY_PAGE = "notiInfo";
    private static final String KEY = "vsim_server_flow_data";
    private static final String TAG = "VirtualSimUtil";

    private VirtualSimUtil() {
    }

    public static Intent getBillIntent(int i) {
        Intent intent = new Intent();
        intent.setPackage("com.miui.virtualsim");
        intent.setAction("com.miui.businesshall.ACTION_ROUTER");
        intent.putExtra("slotId", i);
        intent.putExtra("launchfrom", "securitycenter");
        return intent;
    }

    public static List<String> getSeedSimList(Context context) {
        String string = Settings.Global.getString(context.getContentResolver(), "virtual_seed_sim_net_wlist");
        return TextUtils.isEmpty(string) ? new ArrayList() : Arrays.asList(string.split(","));
    }

    public static boolean isSeedSimEnable(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return TextUtils.equals(str, Settings.Global.getString(context.getContentResolver(), "virtual_seed_sim_iccid"));
    }

    public static VirtualNotiInfo parseNotificationInfo(Context context) {
        VirtualNotiInfo virtualNotiInfo = new VirtualNotiInfo();
        String a2 = f.a(context.getContentResolver(), KEY);
        if (TextUtils.isEmpty(a2)) {
            return virtualNotiInfo;
        }
        try {
            JSONObject jSONObject = new JSONObject(a2).getJSONObject(DataSchemeDataSource.SCHEME_DATA).getJSONObject(ACTION_NOTIFY_PAGE);
            virtualNotiInfo.setTodayUsedTraffic(jSONObject.optString("notiKey1"));
            virtualNotiInfo.setMonthUsedTraffic(jSONObject.optString("notiKey2"));
            virtualNotiInfo.setAcitionDesc(jSONObject.optString("notiKey3"));
            virtualNotiInfo.setAction(jSONObject.optString("action"));
            virtualNotiInfo.setIconUri(jSONObject.optString("iconUri"));
        } catch (JSONException e) {
            Log.i(TAG, "parse virtual sim notification information failed", e);
        }
        return virtualNotiInfo;
    }

    public static VirtualSimInfo parseVirtualSimInfo(Context context) {
        VirtualSimInfo virtualSimInfo = new VirtualSimInfo();
        String a2 = f.a(context.getContentResolver(), KEY);
        if (TextUtils.isEmpty(a2)) {
            return virtualSimInfo;
        }
        try {
            JSONObject jSONObject = new JSONObject(a2).getJSONObject(DataSchemeDataSource.SCHEME_DATA).getJSONObject(ACTION_DETAIL_PAGE);
            virtualSimInfo.setAssistCenter(jSONObject.getJSONObject("assistCenter").optLong(MiStat.Param.CONTENT));
            virtualSimInfo.setAssistCenterTitle(jSONObject.getJSONObject("assistCenter").optString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME));
            virtualSimInfo.setAssistKey1(jSONObject.getJSONObject("assistKey1").optLong(MiStat.Param.CONTENT));
            virtualSimInfo.setAssistKey1Title(jSONObject.getJSONObject("assistKey1").optString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME));
            if (jSONObject.has("assistKey2")) {
                virtualSimInfo.setAssistKey2(jSONObject.getJSONObject("assistKey2").optLong(MiStat.Param.CONTENT));
                virtualSimInfo.setAssistKey2Title(jSONObject.getJSONObject("assistKey2").optString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME));
            }
            if (jSONObject.has("assistBalance")) {
                virtualSimInfo.setAssistBalance(jSONObject.getJSONObject("assistBalance").optLong(MiStat.Param.CONTENT));
                virtualSimInfo.setAssistBalanceTitle(jSONObject.getJSONObject("assistBalance").optString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME));
            }
        } catch (JSONException e) {
            Log.i(TAG, "parse virtual sim UI information failed", e);
        }
        return virtualSimInfo;
    }

    public static void startVirtualSimActivity(Context context, String str) {
        String a2 = f.a(context.getContentResolver(), KEY);
        if (TextUtils.isEmpty(a2)) {
            Log.i(TAG, "start VirtualSim activity json data is null");
            return;
        }
        try {
            Intent parseUri = Intent.parseUri(new JSONObject(a2).getJSONObject(DataSchemeDataSource.SCHEME_DATA).getJSONObject(str).optString("action"), 1);
            parseUri.setFlags(268435456);
            context.startActivity(parseUri);
        } catch (Exception e) {
            Log.e(TAG, "startVirtualSimActivity error!", e);
        }
    }
}
