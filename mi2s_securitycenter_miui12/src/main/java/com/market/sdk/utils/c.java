package com.market.sdk.utils;

import android.text.TextUtils;
import android.util.Log;
import com.miui.luckymoney.model.message.Impl.QQMessage;

public class c {
    public static String a(String str) {
        return "MarketSdk-" + str;
    }

    public static void a(String str, String str2) {
        a(a(str), str2, 0);
    }

    private static void a(String str, String str2, int i) {
        if (TextUtils.isEmpty(str2) || str2.length() <= 3000) {
            b(str, str2, i);
            return;
        }
        int i2 = 0;
        while (i2 <= str2.length() / QQMessage.TYPE_DISCUSS_GROUP) {
            int i3 = i2 * QQMessage.TYPE_DISCUSS_GROUP;
            i2++;
            int min = Math.min(str2.length(), i2 * QQMessage.TYPE_DISCUSS_GROUP);
            if (i3 < min) {
                b(str, str2.substring(i3, min), i);
            }
        }
    }

    private static void b(String str, String str2, int i) {
        if (str2 == null) {
            str2 = "";
        }
        if (i == 0) {
            Log.e(str, str2);
        } else if (i == 1) {
            Log.w(str, str2);
        } else if (i == 2) {
            Log.i(str, str2);
        } else if (i == 3) {
            Log.d(str, str2);
        } else if (i == 4) {
            Log.v(str, str2);
        }
    }
}
