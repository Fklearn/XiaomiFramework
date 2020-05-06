package com.miui.gamebooster.m;

import android.content.ContentResolver;
import com.miui.common.persistence.b;
import java.util.ArrayList;
import miui.provider.ExtraSettings;

public class V {
    public static void a(ContentResolver contentResolver, boolean z) {
        ExtraSettings.Secure.putBoolean(contentResolver, "gamebooster_data_migration", z);
    }

    public static void a(String str, String str2, ArrayList<String> arrayList) {
        ArrayList<String> a2 = b.a(str, arrayList);
        if (a2.contains(str2)) {
            a2.remove(str2);
            b.b(str, a2);
        }
    }

    public static boolean a(ContentResolver contentResolver) {
        return ExtraSettings.Secure.getBoolean(contentResolver, "gamebooster_data_migration", false);
    }

    public static void b(ContentResolver contentResolver, boolean z) {
        ExtraSettings.Secure.putBoolean(contentResolver, "gamebooster_remove_desktop_icon", z);
    }

    public static boolean b(ContentResolver contentResolver) {
        return ExtraSettings.Secure.getBoolean(contentResolver, "gamebooster_remove_desktop_icon", false);
    }
}
