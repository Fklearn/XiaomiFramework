package com.miui.superpower.a;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.miui.powercenter.utils.o;

public class b extends k {

    /* renamed from: c  reason: collision with root package name */
    private static final Uri f8049c = Uri.parse("content://com.android.thememanager.theme_provider");

    public b(Context context, SharedPreferences sharedPreferences) {
        super(context, sharedPreferences);
    }

    private void b(boolean z) {
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean("key_superpower_state", z);
            this.f8065a.getContentResolver().call(f8049c, "changeSuperPowerMode", (String) null, bundle);
        } catch (Exception e) {
            Log.e("SuperPowerSaveManager", "dark theme switch(" + z + ") exception : " + e);
        }
    }

    public void a(boolean z) {
        this.f8066b.edit().putBoolean("pref_key_superpower_darktheme_state", true).commit();
        b(true);
    }

    public boolean a() {
        return !o.m(this.f8065a) && this.f8066b.getBoolean("pref_key_superpower_darktheme_state", false);
    }

    public void c() {
        if (a()) {
            Log.w("SuperPowerSaveManager", "dark theme policy restore state");
            d();
        }
    }

    public void d() {
        if (this.f8066b.getBoolean("pref_key_superpower_darktheme_state", false)) {
            b(false);
            this.f8066b.edit().putBoolean("pref_key_superpower_darktheme_state", false).commit();
        }
    }

    public String name() {
        return "darktheme policy";
    }
}
