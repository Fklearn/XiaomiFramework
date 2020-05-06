package com.xiaomi.analytics.a.a;

import android.text.TextUtils;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

public class p {
    public static String a(String str) {
        return TextUtils.isEmpty(str) ? "" : a(str.getBytes());
    }

    public static String a(byte[] bArr) {
        String str;
        if (bArr != null) {
            try {
                MessageDigest instance = MessageDigest.getInstance("MD5");
                instance.update(bArr);
                str = String.format("%1$032X", new Object[]{new BigInteger(1, instance.digest())});
            } catch (Exception unused) {
            }
            return str.toLowerCase();
        }
        str = "";
        return str.toLowerCase();
    }

    public static <T> T[] a(List<T> list, Class<T> cls) {
        if (list == null || list.size() <= 0) {
            return null;
        }
        T[] tArr = (Object[]) Array.newInstance(cls, list.size());
        for (int i = 0; i < list.size(); i++) {
            tArr[i] = list.get(i);
        }
        return tArr;
    }
}
