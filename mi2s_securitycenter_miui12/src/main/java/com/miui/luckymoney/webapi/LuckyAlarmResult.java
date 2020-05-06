package com.miui.luckymoney.webapi;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.c.d;
import b.b.c.g.a;
import b.b.c.h.j;
import com.miui.luckymoney.config.CommonConfig;
import com.miui.luckymoney.config.Constants;
import com.miui.luckymoney.utils.ImageUtil;
import com.miui.luckymoney.utils.PackageUtil;
import com.miui.luckymoney.utils.ResFileUtils;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LuckyAlarmResult extends RequestResult {
    private static final String TAG = "LuckyAlarmResult";
    private Context context;
    private boolean isLocalConfig;
    private ArrayList<AlarmItem> items;
    private long updateTime;
    private long updateTimeLocal;

    public class AlarmItem {
        public String activityName;
        public Bitmap appIcon;
        public String appIconName;
        public String componentName;
        public long endTime;
        public Intent intent;
        private boolean isTimerRunning;
        public String packageName;
        public long time;
        public String type;
        public String url;

        public AlarmItem() {
        }

        public AlarmItem(boolean z) {
            this.isTimerRunning = z;
        }

        public boolean isTimerRunning() {
            return this.isTimerRunning;
        }

        public void setTimerRunning(boolean z) {
            this.isTimerRunning = z;
        }

        public JSONObject toJSON() {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("activityName", this.activityName);
                jSONObject.put("packageName", this.packageName);
                jSONObject.put("appIcon", this.appIconName);
                jSONObject.put("time", this.time);
                jSONObject.put("endTime", this.endTime);
                jSONObject.put("type", this.type);
                jSONObject.put(MijiaAlertModel.KEY_URL, this.url);
                jSONObject.put("componentName", this.componentName);
                jSONObject.put("isTimerRunning", this.isTimerRunning);
                return jSONObject;
            } catch (JSONException unused) {
                return null;
            }
        }
    }

    public LuckyAlarmResult(String str) {
        super(str);
        this.context = null;
        this.items = new ArrayList<>();
    }

    public LuckyAlarmResult(String str, boolean z) {
        this.context = null;
        this.isLocalConfig = z;
        this.items = new ArrayList<>();
        if (!z) {
            parseJson(str);
        } else {
            parseLocalJson(str);
        }
    }

    private void cancleAllAlarm(Context context2) {
        Log.d(TAG, "cancleAllAlarm");
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_LUCKY_ALARM);
        ((AlarmManager) context2.getSystemService("alarm")).cancel(PendingIntent.getBroadcast(context2, 0, intent, 0));
    }

    private boolean checkServerConfig(JSONObject jSONObject) {
        String optString;
        try {
            this.updateTimeLocal = new JSONObject(CommonConfig.getInstance(this.context).getLuckyAlarmConfig()).optLong("updateTime", 0);
        } catch (Exception unused) {
            this.updateTimeLocal = 0;
        }
        if (this.updateTime <= this.updateTimeLocal) {
            return false;
        }
        ResFileUtils.cleanResDir(this.context, ResFileUtils.LUCKYALARMPATH);
        JSONArray optJSONArray = jSONObject.optJSONArray(Constants.JSON_KEY_CONTENTS);
        if (optJSONArray != null) {
            int length = optJSONArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject optJSONObject = optJSONArray.optJSONObject(i);
                if (!(optJSONObject == null || (optString = optJSONObject.optString("appIcon", (String) null)) == null || optString.length() == 0)) {
                    String resDirPath = ResFileUtils.getResDirPath(this.context, ResFileUtils.LUCKYALARMPATH);
                    a.a(optString, resDirPath, optString.hashCode() + ".png", new j("luckymoney_luckyalarm_downloadres"));
                    try {
                        optJSONObject.put("appIcon", optString.hashCode() + ".png");
                    } catch (JSONException unused2) {
                    }
                }
            }
        }
        cancleAllAlarm(this.context);
        CommonConfig.getInstance(this.context).setLuckyAlarmConfig(jSONObject.toString());
        return true;
    }

    private boolean parseLocalJson(String str) {
        if (TextUtils.isEmpty(str)) {
            this.isSuccess = false;
            return false;
        }
        this.mJsonStr = str;
        JSONObject jSONObject = null;
        try {
            jSONObject = new JSONObject(str);
        } catch (JSONException e) {
            this.isSuccess = false;
            Log.d(TAG, "parseLocalJson failed ", e);
        }
        if (jSONObject == null) {
            this.isSuccess = false;
            return false;
        }
        this.isSuccess = true;
        doParseJson(jSONObject);
        return true;
    }

    public void disableAllItemTimer() {
        if (this.items.size() != 0) {
            Iterator<AlarmItem> it = this.items.iterator();
            while (it.hasNext()) {
                it.next().setTimerRunning(false);
            }
            savetoLocalConfig();
        }
    }

    /* access modifiers changed from: protected */
    public void doParseJson(JSONObject jSONObject) {
        int i;
        JSONArray jSONArray;
        int i2;
        String str;
        String str2;
        long j;
        String string;
        String str3;
        String str4;
        super.doParseJson(jSONObject);
        this.context = d.a();
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        if (isSuccess()) {
            boolean z = this.DEBUG;
            String str5 = TAG;
            if (z) {
                Log.d(str5, "isLocal:" + this.isLocalConfig + " " + jSONObject.toString());
            }
            JSONObject jSONObject2 = jSONObject;
            this.updateTime = jSONObject2.optLong("updateTime", 0);
            if (!this.isLocalConfig && !checkServerConfig(jSONObject)) {
                try {
                    jSONObject2 = new JSONObject(CommonConfig.getInstance(this.context).getLuckyAlarmConfig());
                } catch (Exception unused) {
                    return;
                }
            }
            this.updateTime = jSONObject2.optLong("updateTime", 0);
            JSONArray optJSONArray = jSONObject2.optJSONArray(Constants.JSON_KEY_CONTENTS);
            if (optJSONArray != null) {
                int i3 = 0;
                for (int length = optJSONArray.length(); i3 < length; length = i) {
                    JSONObject optJSONObject = optJSONArray.optJSONObject(i3);
                    if (optJSONObject == null) {
                        jSONArray = optJSONArray;
                        i = length;
                        i2 = i3;
                        str = str5;
                    } else {
                        try {
                            String string2 = optJSONObject.getString("activityName");
                            String string3 = optJSONObject.getString("packageName");
                            String optString = optJSONObject.optString("appIcon", (String) null);
                            long j2 = optJSONObject.getLong("time");
                            i2 = i3;
                            try {
                                j = optJSONObject.getLong("endTime");
                                string = optJSONObject.getString("type");
                                if (MijiaAlertModel.KEY_URL.equals(string)) {
                                    jSONArray = optJSONArray;
                                    str3 = optJSONObject.getString(MijiaAlertModel.KEY_URL);
                                } else {
                                    jSONArray = optJSONArray;
                                    str3 = null;
                                }
                            } catch (Exception e) {
                                e = e;
                                str2 = str5;
                                jSONArray = optJSONArray;
                                i = length;
                                str = str2;
                                Log.d(str, "jsonAlarm解析失败", e);
                                i3 = i2 + 1;
                                str5 = str;
                                optJSONArray = jSONArray;
                            }
                            try {
                                if ("intent".equals(string)) {
                                    i = length;
                                    try {
                                        str4 = optJSONObject.getString("componentName");
                                    } catch (Exception e2) {
                                        e = e2;
                                        str2 = str5;
                                        str = str2;
                                        Log.d(str, "jsonAlarm解析失败", e);
                                        i3 = i2 + 1;
                                        str5 = str;
                                        optJSONArray = jSONArray;
                                    }
                                } else {
                                    i = length;
                                    str4 = null;
                                }
                                if (!optJSONObject.has("isTimerRunning")) {
                                    str2 = str5;
                                    try {
                                        optJSONObject.put("isTimerRunning", false);
                                    } catch (Exception e3) {
                                        e = e3;
                                        str = str2;
                                        Log.d(str, "jsonAlarm解析失败", e);
                                        i3 = i2 + 1;
                                        str5 = str;
                                        optJSONArray = jSONArray;
                                    }
                                } else {
                                    str2 = str5;
                                }
                                AlarmItem alarmItem = new AlarmItem(optJSONObject.getBoolean("isTimerRunning"));
                                alarmItem.activityName = string2;
                                alarmItem.packageName = string3;
                                alarmItem.appIconName = optString;
                                alarmItem.appIcon = optString == null ? null : ImageUtil.loadBitmapfromFile(ResFileUtils.getResFile(this.context, ResFileUtils.LUCKYALARMPATH, optString), this.context);
                                alarmItem.time = j2;
                                alarmItem.endTime = j;
                                alarmItem.type = string;
                                alarmItem.url = str3;
                                alarmItem.componentName = str4;
                                if (MijiaAlertModel.KEY_URL.equals(string)) {
                                    alarmItem.intent = new Intent("android.intent.action.VIEW", Uri.parse(str3));
                                    alarmItem.intent.setFlags(268435456);
                                } else if ("intent".equals(string)) {
                                    ComponentName unflattenFromString = ComponentName.unflattenFromString(str4);
                                    alarmItem.intent = new Intent();
                                    alarmItem.intent.setFlags(268435456);
                                    alarmItem.intent.setComponent(unflattenFromString);
                                } else {
                                    alarmItem.intent = null;
                                }
                                this.items.add(alarmItem);
                                str = str2;
                            } catch (Exception e4) {
                                e = e4;
                                str2 = str5;
                                i = length;
                                str = str2;
                                Log.d(str, "jsonAlarm解析失败", e);
                                i3 = i2 + 1;
                                str5 = str;
                                optJSONArray = jSONArray;
                            }
                        } catch (Exception e5) {
                            e = e5;
                            str2 = str5;
                            jSONArray = optJSONArray;
                            i = length;
                            i2 = i3;
                            str = str2;
                            Log.d(str, "jsonAlarm解析失败", e);
                            i3 = i2 + 1;
                            str5 = str;
                            optJSONArray = jSONArray;
                        }
                    }
                    i3 = i2 + 1;
                    str5 = str;
                    optJSONArray = jSONArray;
                }
                setAlarm(this.context);
                savetoLocalConfig();
            }
        }
    }

    public ArrayList<AlarmItem> getItems() {
        return this.items;
    }

    public void savetoLocalConfig() {
        CommonConfig.getInstance(this.context).setLuckyAlarmConfig(toJSON().toString());
    }

    public void setAlarm(Context context2) {
        int i;
        String str;
        Context context3 = context2;
        if (context3 != null && this.items.size() != 0) {
            long currentTimeMillis = System.currentTimeMillis();
            int size = this.items.size();
            AlarmItem alarmItem = null;
            int i2 = 0;
            while (i2 < size) {
                AlarmItem alarmItem2 = this.items.get(i2);
                long j = alarmItem2.time;
                if (j - 60000 <= currentTimeMillis || j >= 86400000 + currentTimeMillis) {
                    i = i2;
                } else if (!alarmItem2.isTimerRunning()) {
                    if (alarmItem != null) {
                        i = i2;
                        if (Math.abs(alarmItem.time - alarmItem2.time) < 60000) {
                            Log.d(TAG, "shouldn't set LuckyAlarm");
                            return;
                        }
                    } else {
                        i = i2;
                    }
                    AlarmManager alarmManager = (AlarmManager) context3.getSystemService("alarm");
                    Intent intent = new Intent();
                    intent.setAction(Constants.ACTION_LUCKY_ALARM);
                    intent.putExtra("type", alarmItem2.type);
                    String str2 = alarmItem2.type;
                    String str3 = MijiaAlertModel.KEY_URL;
                    if (str3.equals(str2)) {
                        str = alarmItem2.url;
                    } else {
                        str3 = "intent";
                        if (!str3.equals(alarmItem2.type)) {
                            intent.putExtra("activityName", alarmItem2.activityName);
                            alarmManager.setExact(0, alarmItem2.time - 60000, PendingIntent.getBroadcast(context3, 0, intent, 268435456));
                            alarmItem2.setTimerRunning(true);
                            Log.i(TAG, "set LuckyAlarm: " + (alarmItem2.time - 60000));
                            return;
                        } else if (!PackageUtil.isIntentExist(context3, alarmItem2.intent, (String) null)) {
                            alarmItem2 = alarmItem;
                        } else {
                            str = alarmItem2.componentName;
                        }
                    }
                    intent.putExtra(str3, str);
                    intent.putExtra("activityName", alarmItem2.activityName);
                    alarmManager.setExact(0, alarmItem2.time - 60000, PendingIntent.getBroadcast(context3, 0, intent, 268435456));
                    alarmItem2.setTimerRunning(true);
                    Log.i(TAG, "set LuckyAlarm: " + (alarmItem2.time - 60000));
                    return;
                } else {
                    return;
                }
                i2 = i + 1;
                alarmItem = alarmItem2;
            }
        }
    }

    public JSONObject toJSON() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("updateTime", this.updateTime);
            JSONArray jSONArray = new JSONArray();
            Iterator<AlarmItem> it = this.items.iterator();
            while (it.hasNext()) {
                jSONArray.put(it.next().toJSON());
            }
            jSONObject.put(Constants.JSON_KEY_CONTENTS, jSONArray);
            return jSONObject;
        } catch (JSONException unused) {
            return null;
        }
    }
}
