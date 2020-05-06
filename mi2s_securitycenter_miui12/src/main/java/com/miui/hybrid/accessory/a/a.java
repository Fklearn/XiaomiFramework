package com.miui.hybrid.accessory.a;

import android.content.ContentResolver;
import android.provider.Settings;

public class a {
    public static long a(ContentResolver contentResolver) {
        return Settings.Secure.getLong(contentResolver, "ts_user_disable_hybrid_icon_tip", -1);
    }
}
