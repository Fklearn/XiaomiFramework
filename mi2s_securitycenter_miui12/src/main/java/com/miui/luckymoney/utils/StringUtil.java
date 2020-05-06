package com.miui.luckymoney.utils;

public class StringUtil {
    public static String getMaxLengthLimitedString(String str, int i) {
        if (str == null || str.length() <= i) {
            return str;
        }
        return str.substring(0, i) + "...";
    }
}
