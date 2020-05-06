package com.android.server.notification;

public class ZenModeInjector {
    private ZenModeInjector() {
    }

    public static String hideNumbers(String str) {
        StringBuilder sb = new StringBuilder();
        int index = str.indexOf("tel:");
        if (index < 0) {
            return str;
        }
        sb.append(str.substring(index, index + 7));
        for (int i = index + 7; i < str.length(); i++) {
            sb.append('*');
        }
        return sb.toString();
    }
}
