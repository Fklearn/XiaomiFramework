package com.xiaomi.analytics;

import android.util.Log;
import com.xiaomi.analytics.a.a.a;
import com.xiaomi.stat.MiStat;
import org.json.JSONObject;

public class LogEvent {

    /* renamed from: a  reason: collision with root package name */
    private LogType f8261a = LogType.TYPE_EVENT;

    /* renamed from: b  reason: collision with root package name */
    private long f8262b = System.currentTimeMillis();

    /* renamed from: c  reason: collision with root package name */
    private JSONObject f8263c = new JSONObject();

    /* renamed from: d  reason: collision with root package name */
    private JSONObject f8264d = new JSONObject();
    private IdType e = IdType.TYPE_DEFAULT;

    public enum IdType {
        TYPE_DEFAULT(0),
        TYPE_IMEI(1),
        TYPE_MAC(2),
        TYPE_ANDROID_ID(3),
        TYPE_AAID(4),
        TYPE_GAID(5),
        TYPE_GUID(6);
        
        private int i;

        private IdType(int i2) {
            this.i = 0;
            this.i = i2;
        }

        public int a() {
            return this.i;
        }
    }

    public enum LogType {
        TYPE_EVENT(0),
        TYPE_AD(1);
        

        /* renamed from: d  reason: collision with root package name */
        private int f8272d;

        private LogType(int i) {
            this.f8272d = 0;
            this.f8272d = i;
        }

        public int a() {
            return this.f8272d;
        }
    }

    public LogEvent() {
    }

    public LogEvent(LogType logType) {
        if (logType != null) {
            this.f8261a = logType;
        }
    }

    public static LogEvent a() {
        return new LogEvent();
    }

    public static LogEvent a(LogType logType) {
        return new LogEvent(logType);
    }

    /* access modifiers changed from: package-private */
    public LogEvent a(JSONObject jSONObject) {
        if (jSONObject != null) {
            this.f8264d = jSONObject;
        }
        return this;
    }

    public String a(String str, String str2, String str3) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("v", 2);
            jSONObject.put("appId", str);
            jSONObject.put("sessionId", str3);
            jSONObject.put("configKey", str2);
            jSONObject.put(MiStat.Param.CONTENT, this.f8263c.toString());
            jSONObject.put("eventTime", this.f8262b);
            jSONObject.put("logType", this.f8261a.a());
            jSONObject.put("extra", this.f8264d.toString());
            jSONObject.put("idType", this.e.a());
        } catch (Exception e2) {
            Log.e(a.a("LogEvent"), "pack e", e2);
        }
        return jSONObject.toString();
    }

    /* access modifiers changed from: package-private */
    public LogEvent b(JSONObject jSONObject) {
        if (jSONObject != null) {
            this.f8263c = jSONObject;
        }
        return this;
    }
}
