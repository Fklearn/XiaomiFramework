package com.xiaomi.analytics;

public class Actions {
    public static AdAction a(String str) {
        return new AdAction(str);
    }

    public static AdAction a(String str, String str2) {
        return new AdAction(str, str2);
    }

    public static CustomAction a() {
        return new CustomAction();
    }

    public static EventAction b(String str) {
        return new EventAction(str);
    }

    public static EventAction b(String str, String str2) {
        return new EventAction(str, str2);
    }
}
