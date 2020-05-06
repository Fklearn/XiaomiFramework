package com.miui.internal.content.res;

public class ThemeToolUtils {
    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

    static boolean equals(String a, String b) {
        if (a != null) {
            return a.equals(b);
        }
        return a == b;
    }

    public static String getNameFromPath(String path) {
        int index = path.lastIndexOf(47);
        if (index >= 0) {
            return path.substring(index + 1);
        }
        return path;
    }
}
