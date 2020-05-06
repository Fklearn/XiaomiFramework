package com.miui.superpower.a;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import b.b.o.g.d;
import com.miui.activityutil.o;
import com.miui.googlebase.b.b;
import com.miui.superpower.a;
import java.util.HashSet;
import java.util.Set;
import miui.cloud.os.SystemProperties;

public class c extends k {

    /* renamed from: c  reason: collision with root package name */
    private static final Set<String> f8050c = new HashSet();

    /* renamed from: d  reason: collision with root package name */
    private static final Set<String> f8051d = new HashSet();

    static {
        f8050c.add("com.miui.voiceassist");
        f8050c.add("com.miui.systemAdSolution");
        f8051d.add("com.android.vending");
        f8051d.add("com.google.android.gms");
        f8051d.add("com.google.android.gsf");
        f8051d.add("com.google.android.syncadapters.contacts");
        f8051d.add("com.google.android.backuptransport");
        f8051d.add("com.google.android.onetimeinitializer");
        f8051d.add("com.google.android.partnersetup");
        f8051d.add("com.google.android.configupdater");
        f8051d.add("com.google.android.ext.services");
        f8051d.add("com.google.android.ext.shared");
        f8051d.add("com.google.android.printservice.recommendation");
        a.b(f8050c);
        a.a(f8051d);
    }

    public c(Context context, SharedPreferences sharedPreferences) {
        super(context, sharedPreferences);
    }

    private int a(String str) {
        try {
            return this.f8065a.getPackageManager().getApplicationEnabledSetting(str);
        } catch (Exception e) {
            Log.e("SuperPowerSaveManager", "disablepkg policy exception : " + e);
            return -1;
        }
    }

    private void a(String str, int i) {
        try {
            this.f8065a.getPackageManager().setApplicationEnabledSetting(str, i, 1);
        } catch (Exception e) {
            Log.w("SuperPowerSaveManager", "enablePkg policy exception : " + e);
        }
    }

    private void a(String str, ActivityManager activityManager) {
        try {
            this.f8065a.getPackageManager().setApplicationEnabledSetting(str, 2, 1);
            d.a("SuperPowerSaveManager", (Object) activityManager, "forceStopPackage", (Class<?>[]) new Class[]{String.class}, str);
        } catch (Exception e) {
            Log.e("SuperPowerSaveManager", "disablepkg policy exception : " + e);
        }
    }

    private boolean e() {
        return o.f2310b.equals(SystemProperties.get("ro.miui.has_gmscore")) || b.b(this.f8065a);
    }

    public void a(boolean z) {
        ActivityManager activityManager = (ActivityManager) this.f8065a.getSystemService("activity");
        HashSet hashSet = new HashSet();
        HashSet hashSet2 = new HashSet();
        HashSet<String> hashSet3 = new HashSet<>(f8050c);
        if (e()) {
            hashSet3.addAll(f8051d);
        }
        if (this.f8066b.getBoolean("pref_key_superpower_disabled_state", false)) {
            Log.w("SuperPowerSaveManager", "enter superpower but last not exit normal");
            hashSet3.removeAll(this.f8066b.getStringSet("pref_key_superpower_disabled_backup_enable", new HashSet()));
            hashSet3.removeAll(this.f8066b.getStringSet("pref_key_superpower_disabled_backup_default", new HashSet()));
        }
        for (String str : hashSet3) {
            int a2 = a(str);
            if (a2 != -1 && (a2 == 1 || a2 == 0)) {
                SharedPreferences.Editor edit = this.f8066b.edit();
                edit.putBoolean("pref_key_superpower_disabled_state", true);
                if (a2 == 1) {
                    hashSet.add(str);
                    edit.putStringSet("pref_key_superpower_disabled_backup_enable", hashSet);
                } else {
                    hashSet2.add(str);
                    edit.putStringSet("pref_key_superpower_disabled_backup_default", hashSet2);
                }
                edit.commit();
                a(str, activityManager);
            }
        }
    }

    public boolean a() {
        return !com.miui.powercenter.utils.o.m(this.f8065a) && this.f8066b.getBoolean("pref_key_superpower_disabled_state", false);
    }

    public void c() {
        if (a()) {
            Log.w("SuperPowerSaveManager", "resotre disable state");
            d();
        }
    }

    public void d() {
        int a2;
        if (this.f8066b.getBoolean("pref_key_superpower_disabled_state", false)) {
            for (String a3 : this.f8066b.getStringSet("pref_key_superpower_disabled_backup_enable", new HashSet())) {
                a(a3, 1);
            }
            for (String a4 : this.f8066b.getStringSet("pref_key_superpower_disabled_backup_default", new HashSet())) {
                a(a4, 0);
            }
            SharedPreferences.Editor edit = this.f8066b.edit();
            edit.putBoolean("pref_key_superpower_disabled_state", false);
            edit.putStringSet("pref_key_superpower_disabled_backup_enable", new HashSet());
            edit.putStringSet("pref_key_superpower_disabled_backup_default", new HashSet());
            edit.commit();
        }
        for (String next : f8050c) {
            if (!(!((Boolean) d.a("SuperPowerSaveManager", (Object) this.f8065a.getPackageManager(), "isPackageAvailable", (Class<?>[]) new Class[]{String.class}, next)).booleanValue() || (a2 = a(next)) == 0 || a2 == 1)) {
                Log.e("SuperPowerSaveManager", "disable(" + next + ") not restore state");
                this.f8065a.getPackageManager().setApplicationEnabledSetting(next, 0, 1);
            }
        }
    }

    public String name() {
        return "disablepkg policy";
    }
}
