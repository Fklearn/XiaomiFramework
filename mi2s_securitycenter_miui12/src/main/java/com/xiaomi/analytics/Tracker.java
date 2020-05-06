package com.xiaomi.analytics;

import com.xiaomi.analytics.LogEvent;

public class Tracker extends BaseLogger {
    Tracker(String str) {
        super(str);
    }

    public void a(Action action) {
        if (action != null) {
            a((action instanceof AdAction ? LogEvent.a(LogEvent.LogType.TYPE_AD) : LogEvent.a()).b(action.a()).a(action.b()));
        }
    }
}
