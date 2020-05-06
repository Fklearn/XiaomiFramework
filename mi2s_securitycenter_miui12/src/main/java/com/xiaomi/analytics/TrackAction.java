package com.xiaomi.analytics;

public class TrackAction extends Action {
    public TrackAction b(String str) {
        a("_action_", (Object) str);
        return this;
    }

    public TrackAction c(String str) {
        a("_category_", (Object) str);
        return this;
    }
}
