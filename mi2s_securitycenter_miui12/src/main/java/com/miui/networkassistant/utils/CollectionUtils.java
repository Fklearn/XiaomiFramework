package com.miui.networkassistant.utils;

import android.text.TextUtils;

public class CollectionUtils {
    private CollectionUtils() {
    }

    public static int getArrayIndex(CharSequence[] charSequenceArr, String str) {
        for (int i = 0; i < charSequenceArr.length; i++) {
            if (TextUtils.equals(charSequenceArr[i], str)) {
                return i;
            }
        }
        return -1;
    }
}
