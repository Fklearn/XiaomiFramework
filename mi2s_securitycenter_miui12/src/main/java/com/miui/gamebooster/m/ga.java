package com.miui.gamebooster.m;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class ga {
    public static Long a(String str, String str2) {
        try {
            return Long.valueOf(new SimpleDateFormat(str2).parse(str).getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String a(Long l) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(l);
    }

    public static String a(String str) {
        int length = str.length();
        char[] cArr = new char[(length << 1)];
        int i = 0;
        int i2 = 0;
        while (i < length) {
            cArr[i2] = str.charAt(i);
            cArr[i2 + 1] = ' ';
            i++;
            i2 = i << 1;
        }
        return new String(cArr);
    }

    public static int b(String str) {
        String trim = Pattern.compile("[^0-9]").matcher(str).replaceAll("").trim();
        if (trim.length() > 0) {
            return Integer.valueOf(trim).intValue();
        }
        return -1;
    }
}
