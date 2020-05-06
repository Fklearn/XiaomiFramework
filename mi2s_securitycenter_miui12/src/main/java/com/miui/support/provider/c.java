package com.miui.support.provider;

import android.content.ContentResolver;
import android.provider.Settings;
import android.provider.SystemSettings;

public class c extends SystemSettings.Secure {
    public static boolean a(ContentResolver contentResolver, String str, boolean z) {
        return Settings.Secure.getInt(contentResolver, str, z ? 1 : 0) != 0;
    }
}
