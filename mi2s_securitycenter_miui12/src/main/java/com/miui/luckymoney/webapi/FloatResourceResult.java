package com.miui.luckymoney.webapi;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.c.d;
import b.b.c.g.a;
import b.b.c.h.j;
import b.b.c.j.B;
import com.miui.luckymoney.config.CommonConfig;
import com.miui.luckymoney.config.Constants;
import com.miui.luckymoney.stats.MiStatUtil;
import com.miui.luckymoney.utils.ResFileUtils;
import com.miui.networkassistant.provider.ProviderConstant;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FloatResourceResult extends RequestResult {
    private static String TAG = "FloatResourceResult";
    private long adsUpdateTime;
    private Context context;
    private long floatActivityDefaultUpdateTime;
    private long floatActivityUpdateTime;
    private long floatTipsUpdateTime;
    private boolean isFloatTipsUpdate;
    private JSONObject jsonAds;
    private JSONObject jsonFloatActivity;
    private JSONObject jsonFloatActivityDefault;
    private JSONObject jsonFloatTips;
    private CommonConfig mCommonConfig;

    public FloatResourceResult(String str) {
        super(str);
    }

    /* access modifiers changed from: protected */
    public void doParseJson(JSONObject jSONObject) {
        JSONArray optJSONArray;
        JSONArray optJSONArray2;
        int i;
        JSONArray jSONArray;
        String str;
        JSONObject jSONObject2 = jSONObject;
        super.doParseJson(jSONObject);
        if (isSuccess()) {
            if (this.DEBUG) {
                Log.d(TAG, jSONObject.toString());
            }
            this.context = d.a();
            this.mCommonConfig = CommonConfig.getInstance(this.context);
            this.jsonFloatActivity = jSONObject2.optJSONObject(Constants.JSON_KEY_FLOATACTIVITY);
            this.jsonFloatTips = jSONObject2.optJSONObject(Constants.JSON_KEY_FLOATTIPS);
            this.jsonFloatActivityDefault = jSONObject2.optJSONObject(Constants.JSON_KEY_FLOATACTIVITYDEFAULT);
            this.jsonAds = jSONObject2.optJSONObject("Ads");
            try {
                this.floatActivityUpdateTime = new JSONObject(this.mCommonConfig.getFloatAssistantConfig()).getLong("updateTime");
            } catch (Exception unused) {
                this.floatActivityUpdateTime = 0;
            }
            try {
                this.floatTipsUpdateTime = new JSONObject(this.mCommonConfig.getFloatTipsConfig()).getLong("updateTime");
            } catch (Exception unused2) {
                this.floatTipsUpdateTime = 0;
            }
            try {
                this.adsUpdateTime = new JSONObject(this.mCommonConfig.getAdsConfig()).getLong("updateTime");
            } catch (Exception unused3) {
                this.adsUpdateTime = 0;
            }
            try {
                this.floatActivityDefaultUpdateTime = new JSONObject(this.mCommonConfig.getFloatActivityDefaultConfig()).getLong("updateTime");
            } catch (Exception unused4) {
                this.floatActivityDefaultUpdateTime = 0;
            }
            JSONObject jSONObject3 = this.jsonFloatActivity;
            String str2 = null;
            if (!(jSONObject3 == null || jSONObject3.optLong("updateTime", 0) <= this.floatActivityUpdateTime || (optJSONArray2 = this.jsonFloatActivity.optJSONArray(Constants.JSON_KEY_CONTENTS)) == null)) {
                ResFileUtils.cleanResDir(this.context, ResFileUtils.FLOAT5);
                int length = optJSONArray2.length();
                int i2 = 0;
                while (i2 < length) {
                    JSONObject optJSONObject = optJSONArray2.optJSONObject(i2);
                    if (optJSONObject != null) {
                        try {
                            String optString = optJSONObject.optString("icon1", str2);
                            String optString2 = optJSONObject.optString("icon2", str2);
                            if (optString != null) {
                                if (optString2 != null) {
                                    String resDirPath = ResFileUtils.getResDirPath(this.context, ResFileUtils.FLOAT5);
                                    jSONArray = optJSONArray2;
                                    try {
                                        StringBuilder sb = new StringBuilder();
                                        i = length;
                                        try {
                                            sb.append(optString.hashCode());
                                            sb.append(".png");
                                            a.a(optString, resDirPath, sb.toString(), new j("luckymoney_floatwindow_downloadres"));
                                            a.a(optString2, ResFileUtils.getResDirPath(this.context, ResFileUtils.FLOAT5), optString2.hashCode() + ".png", new j("luckymoney_floatwindow_downloadres"));
                                            optJSONObject.put("icon2", optString2.hashCode() + ".png");
                                            str = optString.hashCode() + ".png";
                                            optJSONObject.put("icon1", str);
                                        } catch (JSONException unused5) {
                                        }
                                    } catch (JSONException unused6) {
                                    }
                                    i2++;
                                    optJSONArray2 = jSONArray;
                                    length = i;
                                    str2 = null;
                                }
                            }
                            jSONArray = optJSONArray2;
                            i = length;
                            str = null;
                            optJSONObject.put("icon2", (Object) null);
                            optJSONObject.put("icon1", str);
                        } catch (JSONException unused7) {
                        }
                        i2++;
                        optJSONArray2 = jSONArray;
                        length = i;
                        str2 = null;
                    }
                    jSONArray = optJSONArray2;
                    i = length;
                    i2++;
                    optJSONArray2 = jSONArray;
                    length = i;
                    str2 = null;
                }
                this.mCommonConfig.setFloatAssistantConfig(this.jsonFloatActivity.toString());
                if (this.DEBUG) {
                    Log.d(TAG, "jsonFloatActivity:" + this.jsonFloatActivity.toString());
                }
            }
            JSONObject jSONObject4 = this.jsonFloatTips;
            if (jSONObject4 != null && jSONObject4.optLong("updateTime", 0) > this.floatTipsUpdateTime) {
                this.isFloatTipsUpdate = true;
                JSONArray optJSONArray3 = this.jsonFloatTips.optJSONArray(Constants.JSON_KEY_CONTENTS);
                if (optJSONArray3 == null) {
                    this.isFloatTipsUpdate = false;
                } else {
                    ResFileUtils.cleanResDir(this.context, ResFileUtils.FLOATTIPS);
                    int length2 = optJSONArray3.length();
                    for (int i3 = 0; i3 < length2; i3++) {
                        JSONObject optJSONObject2 = optJSONArray3.optJSONObject(i3);
                        if (optJSONObject2 != null) {
                            try {
                                String string = optJSONObject2.getString("iconLeft");
                                String string2 = optJSONObject2.getString("iconRight");
                                a.a(string, ResFileUtils.getResDirPath(this.context, ResFileUtils.FLOATTIPS), string.hashCode() + ".png", new j("luckymoney_floatwindow_downloadres"));
                                a.a(string2, ResFileUtils.getResDirPath(this.context, ResFileUtils.FLOATTIPS), string2.hashCode() + ".png", new j("luckymoney_floatwindow_downloadres"));
                                optJSONObject2.put("iconLeft", string.hashCode() + ".png");
                                optJSONObject2.put("iconRight", string2.hashCode() + ".png");
                            } catch (JSONException unused8) {
                            }
                        }
                    }
                    this.mCommonConfig.setFloatTipsConfig(this.jsonFloatTips.toString());
                    if (this.DEBUG) {
                        Log.d(TAG, "jsonFloatTips:" + this.jsonFloatTips.toString());
                    }
                }
            }
            JSONObject jSONObject5 = this.jsonAds;
            if (!(jSONObject5 == null || jSONObject5.optLong("updateTime", 0) <= this.adsUpdateTime || (optJSONArray = this.jsonAds.optJSONArray(Constants.JSON_KEY_CONTENTS)) == null)) {
                ResFileUtils.cleanResDir(this.context, "Ads");
                int length3 = optJSONArray.length();
                for (int i4 = 0; i4 < length3; i4++) {
                    JSONObject optJSONObject3 = optJSONArray.optJSONObject(i4);
                    if (optJSONObject3 != null) {
                        String str3 = null;
                        try {
                            String optString3 = optJSONObject3.optString(ProviderConstant.DataUsageNotiStatusColumns.COLUMN_ICON, (String) null);
                            if (optString3 != null) {
                                a.a(optString3, ResFileUtils.getResDirPath(this.context, "Ads"), optString3.hashCode() + ".png", new j("luckymoney_floatwindow_downloadres"));
                                str3 = optString3.hashCode() + ".png";
                            }
                            optJSONObject3.put(ProviderConstant.DataUsageNotiStatusColumns.COLUMN_ICON, str3);
                        } catch (JSONException unused9) {
                        }
                    }
                }
                this.mCommonConfig.setAdsConfig(this.jsonAds.toString());
                if (this.DEBUG) {
                    Log.d(TAG, "jsonAds:" + this.jsonAds.toString());
                }
            }
            JSONObject jSONObject6 = this.jsonFloatActivityDefault;
            if (jSONObject6 != null && jSONObject6.optLong("updateTime", 0) > this.floatActivityDefaultUpdateTime) {
                ResFileUtils.cleanResDir(this.context, ResFileUtils.FLOAT34);
                JSONObject optJSONObject4 = this.jsonFloatActivityDefault.optJSONObject(MiStatUtil.KEY_LUCK_MONEY_REMINDED_WEIXIN_POSTFIX);
                JSONObject optJSONObject5 = this.jsonFloatActivityDefault.optJSONObject("alipay");
                if (optJSONObject4 != null) {
                    String optString4 = optJSONObject4.optString(ProviderConstant.DataUsageNotiStatusColumns.COLUMN_ICON, (String) null);
                    if (!TextUtils.isEmpty(optString4)) {
                        a.a(optString4, ResFileUtils.getResDirPath(this.context, ResFileUtils.FLOAT34), optString4.hashCode() + ".png", new j("luckymoney_floatwindow_downloadres"));
                        try {
                            optJSONObject4.put(ProviderConstant.DataUsageNotiStatusColumns.COLUMN_ICON, optString4.hashCode() + ".png");
                            this.jsonFloatActivityDefault.put(MiStatUtil.KEY_LUCK_MONEY_REMINDED_WEIXIN_POSTFIX, optJSONObject4);
                        } catch (JSONException unused10) {
                        }
                    }
                }
                if (optJSONObject5 != null) {
                    String optString5 = optJSONObject5.optString(ProviderConstant.DataUsageNotiStatusColumns.COLUMN_ICON, (String) null);
                    if (!TextUtils.isEmpty(optString5)) {
                        a.a(optString5, ResFileUtils.getResDirPath(this.context, ResFileUtils.FLOAT34), optString5.hashCode() + ".png", new j("luckymoney_floatwindow_downloadres"));
                        try {
                            optJSONObject5.put(ProviderConstant.DataUsageNotiStatusColumns.COLUMN_ICON, optString5.hashCode() + ".png");
                            this.jsonFloatActivityDefault.put("alipay", optJSONObject5);
                        } catch (JSONException unused11) {
                        }
                    }
                }
                this.mCommonConfig.setFloatActivityDefaultConfig(this.jsonFloatActivityDefault.toString());
                if (this.DEBUG) {
                    Log.d(TAG, "jsonDefault:" + this.jsonFloatActivityDefault.toString());
                }
            }
            if (this.isFloatTipsUpdate) {
                Intent intent = new Intent();
                intent.setAction(Constants.ACTION_UPDATE_TIPS_CONFIG);
                this.context.sendBroadcastAsUser(intent, B.b());
            }
        }
    }
}
