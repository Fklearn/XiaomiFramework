package com.xiaomi.analytics.a.b;

import com.xiaomi.analytics.a.m;

public interface a {
    boolean a(String str);

    String b(String str);

    m getVersion();

    void init();

    void setDebugOn(boolean z);

    void setDefaultPolicy(String str, String str2);

    void trackEvent(String str);

    void trackEvents(String[] strArr);
}
