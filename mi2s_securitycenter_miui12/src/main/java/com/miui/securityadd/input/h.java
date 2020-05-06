package com.miui.securityadd.input;

import java.util.HashMap;
import java.util.Map;

public class h {

    /* renamed from: a  reason: collision with root package name */
    private static Map<String, String> f7457a = new HashMap();

    /* renamed from: b  reason: collision with root package name */
    private static String[] f7458b = {"e412", "e112", "e312", "e14c", "e41d", "e11b", "e40e", "e403", "e107", "e40d", "e409", "e40c", "e415"};

    /* renamed from: c  reason: collision with root package name */
    private static String[] f7459c = {"1f602", "1f381", "1f389", "1f4aa", "1f64f", "1f47b", "1f612", "1f614", "1f631", "1f633", "1f61d", "1f637", "1f604"};

    static {
        int i = 0;
        while (true) {
            String[] strArr = f7458b;
            if (i < strArr.length) {
                f7457a.put(strArr[i], f7459c[i]);
                i++;
            } else {
                return;
            }
        }
    }

    private static String a(char c2) {
        String lowerCase = Integer.toHexString(c2).toLowerCase();
        return f7457a.containsKey(lowerCase) ? f7457a.get(lowerCase) : Integer.toHexString(c2);
    }

    private static String a(CharSequence charSequence) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < charSequence.length(); i++) {
            char charAt = charSequence.charAt(i);
            stringBuffer.append("\\u" + a(charAt));
        }
        return stringBuffer.toString();
    }

    public static String a(String str) {
        return b(a((CharSequence) str));
    }

    private static String b(String str) {
        StringBuffer stringBuffer = new StringBuffer();
        String[] split = str.split("\\\\u");
        for (int i = 1; i < split.length; i++) {
            stringBuffer.append(Character.toChars(Integer.parseInt(split[i], 16)));
        }
        return stringBuffer.toString();
    }
}
