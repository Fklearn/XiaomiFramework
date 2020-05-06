package com.xiaomi.analytics;

import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.analytics.a.a.a;

public class EventAction extends Action {

    /* renamed from: d  reason: collision with root package name */
    private String f8260d;

    public EventAction(String str) {
        this(str, (String) null);
    }

    public EventAction(String str, String str2) {
        if (TextUtils.isEmpty(str)) {
            Log.w(a.a("EventAction"), "eventName is null when constructing EventAction!");
        }
        this.f8260d = str;
        a(this.f8260d);
        if (str2 != null && !TextUtils.isEmpty(str2)) {
            b("_event_default_param_", str2);
        }
    }
}
