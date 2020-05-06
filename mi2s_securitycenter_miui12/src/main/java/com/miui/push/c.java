package com.miui.push;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.j.B;
import b.b.c.j.g;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.miui.earthquakewarning.Constants;
import com.miui.earthquakewarning.EarthquakeWarningManager;
import com.miui.earthquakewarning.analytics.AnalyticHelper;
import com.miui.earthquakewarning.utils.SHA1WithRSAUtil;
import com.miui.luckymoney.utils.LuckyPushUtil;
import com.miui.securitycenter.dynamic.DynamicServiceManager;
import com.miui.warningcenter.WarningCenterManager;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import com.miui.warningcenter.mijia.MijiaConstants;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.stat.MiStat;
import miui.os.Build;
import org.json.JSONArray;
import org.json.JSONObject;

class c implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MiPushMessage f7442a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Context f7443b;

    c(MiPushMessage miPushMessage, Context context) {
        this.f7442a = miPushMessage;
        this.f7443b = context;
    }

    public void run() {
        String a2;
        JSONArray jSONArray;
        try {
            JSONObject jSONObject = new JSONObject(this.f7442a.getContent());
            if (jSONObject.has(MiStat.Param.CONTENT)) {
                String string = jSONObject.getString(MiStat.Param.CONTENT);
                if (TextUtils.isEmpty(string)) {
                    return;
                }
                if (string.contains("SecurityCenterPassThroughMessage")) {
                    jSONObject = new JSONObject(string);
                } else {
                    return;
                }
            }
            if (jSONObject.has("SecurityCenterPassThroughMessage")) {
                JSONObject jSONObject2 = jSONObject.getJSONObject("SecurityCenterPassThroughMessage");
                String string2 = jSONObject2.getString("moduleName");
                if (TextUtils.equals(string2, "wakePath")) {
                    if (jSONObject2.getInt("version") == 1 && (jSONArray = jSONObject2.getJSONArray("commands")) != null) {
                        for (int i = 0; i < jSONArray.length(); i++) {
                            JSONObject jSONObject3 = new JSONObject(jSONArray.getString(i));
                            String string3 = jSONObject3.getString("command");
                            if (!TextUtils.isEmpty(string3)) {
                                if (TextUtils.equals(string3, "UpdateWakePathData")) {
                                    Intent intent = new Intent("miui.intent.action.DELAY_UPDATE_WAKEPATH");
                                    intent.putExtra("channel", "push");
                                    if (jSONObject3.getBoolean("forceUpdate")) {
                                        intent.putExtra("forceUpdate", true);
                                    }
                                    intent.setPackage("com.lbe.security.miui");
                                    g.a(this.f7443b, intent, B.k());
                                }
                            }
                        }
                    }
                } else if (TextUtils.equals(string2, "monthReport")) {
                    if (jSONObject2.getInt("version") == 1 && com.miui.monthreport.g.b() && !Build.IS_INTERNATIONAL_BUILD) {
                        String title = this.f7442a.getTitle();
                        String description = this.f7442a.getDescription();
                        String string4 = jSONObject2.getString(DataSchemeDataSource.SCHEME_DATA);
                        Intent intent2 = new Intent();
                        XiaomiPushReceiver.b(this.f7443b, PendingIntent.getActivity(this.f7443b, 10006, intent2.setData(Uri.parse("securitycenter://monthreport?content=" + string4)), 1073741824), title, description);
                    }
                } else if ("dynamic".equals(string2)) {
                    if ("update".equals(jSONObject2.optString("cmd"))) {
                        DynamicServiceManager.getInstance(this.f7443b).update(true);
                    }
                } else if ("antivirus".equals(string2)) {
                    b.b.b.g.a(this.f7443b).a(jSONObject2.optJSONObject("info"));
                } else if ("luckymoney".equals(string2)) {
                    LuckyPushUtil.processCMD(this.f7443b, jSONObject2);
                } else if (!"cloudControl".equals(string2)) {
                    if ("earthquakeWarning".equals(string2)) {
                        if (jSONObject2.optJSONObject("quake") != null) {
                            Log.i(XiaomiPushReceiver.f7436a, "receive quake message");
                            JSONObject jSONObject4 = jSONObject2.getJSONObject("quake");
                            if (SHA1WithRSAUtil.virefy(jSONObject4.toString(), jSONObject2.optString("quakeSign"), Constants.EARTHQUAKE_PUBKEY)) {
                                EarthquakeWarningManager.getInstance().showWarningInfo(this.f7443b, jSONObject4);
                            } else {
                                a2 = XiaomiPushReceiver.f7436a;
                                Log.i(a2, "quakeSign failed!");
                                return;
                            }
                        } else {
                            return;
                        }
                    } else if ("MijiaWarning".equals(string2) && jSONObject2.optJSONObject(MijiaAlertModel.KEY_WARNING) != null) {
                        Log.i(XiaomiPushReceiver.f7436a, "receive warning message");
                        JSONObject jSONObject5 = jSONObject2.getJSONObject(MijiaAlertModel.KEY_WARNING);
                        if (SHA1WithRSAUtil.virefy(jSONObject2.optString(MijiaAlertModel.KEY_WARNING).replace("\\", ""), jSONObject2.optString(MijiaAlertModel.KEY_WARINGSIGN), MijiaConstants.MIJIA_PUBKEY)) {
                            WarningCenterManager.getInstance().parseQuake(this.f7443b, jSONObject5);
                        } else {
                            a2 = XiaomiPushReceiver.f7436a;
                            Log.i(a2, "quakeSign failed!");
                            return;
                        }
                    } else {
                        return;
                    }
                    AnalyticHelper.trackPushActionModuleClick(AnalyticHelper.PUSH_RECEIVE);
                } else if (jSONObject2.optInt("version") == 1) {
                    Intent intent3 = new Intent("com.android.settings.action.PULL_CLOUD_DATA");
                    intent3.addFlags(16777216);
                    this.f7443b.sendBroadcast(intent3);
                }
            }
        } catch (Exception e) {
            Log.e(XiaomiPushReceiver.f7436a, "passThroughPaser", e);
        }
    }
}
