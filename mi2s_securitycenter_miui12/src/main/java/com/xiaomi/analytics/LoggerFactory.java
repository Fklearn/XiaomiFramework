package com.xiaomi.analytics;

import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.analytics.BaseLogger;
import com.xiaomi.analytics.a.a.a;
import java.util.concurrent.ConcurrentHashMap;

class LoggerFactory<T extends BaseLogger> {

    /* renamed from: a  reason: collision with root package name */
    public ConcurrentHashMap<String, T> f8273a = new ConcurrentHashMap<>();

    public T a(Class<T> cls, String str) {
        if (TextUtils.isEmpty(str) || cls == null) {
            throw new IllegalArgumentException("Clazz is null or configKey is empty. configKey:" + str);
        }
        T t = (BaseLogger) this.f8273a.get(str);
        if (t != null) {
            return t;
        }
        try {
            T t2 = (BaseLogger) cls.getDeclaredConstructor(new Class[]{String.class}).newInstance(new Object[]{str});
            this.f8273a.put(str, t2);
            return t2;
        } catch (Exception e) {
            Log.e(a.a("LoggerFactory"), "getLogger e", e);
            throw new IllegalStateException("Can not instantiate logger. configKey:" + str);
        }
    }
}
