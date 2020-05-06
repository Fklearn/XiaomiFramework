package com.miui.superpower.a;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;
import b.b.b.p;
import b.b.o.g.e;
import com.miui.antivirus.service.GuardService;
import com.miui.powercenter.utils.o;
import com.miui.superpower.b.k;

public class i extends k {

    /* renamed from: c  reason: collision with root package name */
    private static final Uri f8062c = Uri.parse("content://com.miui.luckymoney.provider/lmEnable");

    public i(Context context, SharedPreferences sharedPreferences) {
        super(context, sharedPreferences);
    }

    private void b(boolean z) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("enable", Boolean.valueOf(z));
        this.f8065a.getContentResolver().update(f8062c, contentValues, (String) null, (String[]) null);
    }

    private void c(boolean z) {
        Intent intent = new Intent(this.f8065a, GuardService.class);
        intent.setAction(z ? "action_register_foreground_notification" : "action_unregister_foreground_notification");
        this.f8065a.startService(intent);
    }

    private boolean e() {
        int columnIndex;
        Cursor cursor = null;
        try {
            cursor = this.f8065a.getContentResolver().query(f8062c, (String[]) null, (String) null, (String[]) null, (String) null);
            if (cursor != null && cursor.moveToFirst() && (columnIndex = cursor.getColumnIndex("enable")) >= 0) {
                return Boolean.parseBoolean(cursor.getString(columnIndex));
            }
            if (cursor == null) {
                return false;
            }
            cursor.close();
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void f() {
        UserManager userManager;
        int intValue;
        if (k.b() && (userManager = (UserManager) this.f8065a.getSystemService("user")) != null && userManager.getUserCount() > 1) {
            try {
                Class<?> cls = Class.forName("android.app.IStopUserCallback");
                Object invoke = Class.forName("android.app.ActivityManagerNative").getMethod("getDefault", new Class[0]).invoke((Object) null, new Object[0]);
                Object a2 = e.a((Object) userManager, "getUsers", (Class<?>[]) null, new Object[0]);
                if (a2 != null && (intValue = ((Integer) e.a(a2, Integer.TYPE, "size", (Class<?>[]) null, new Object[0])).intValue()) > 1) {
                    for (int i = 0; i < intValue; i++) {
                        int intValue2 = ((Integer) e.a(e.a(a2, "get", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(i)), "id", Integer.TYPE)).intValue();
                        if (!(intValue2 == 0 || intValue2 == 999)) {
                            Log.w("SuperPowerSaveManager", "stop user : " + intValue2);
                            e.a(invoke, "stopUser", (Class<?>[]) new Class[]{Integer.TYPE, Boolean.TYPE, cls}, Integer.valueOf(intValue2), true, null);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("SuperPowerSaveManager", "stop user exception : " + e);
            }
        }
    }

    public void a(boolean z) {
        if (!this.f8066b.getBoolean("pref_key_superpower_othersetting_state", false)) {
            boolean e = e();
            SharedPreferences.Editor edit = this.f8066b.edit();
            if (e) {
                edit.putBoolean("pref_key_superpower_luckymoney_state", true);
                edit.putBoolean("pref_key_superpower_othersetting_state", true);
                edit.commit();
                b(false);
            }
            if (p.j()) {
                edit.putBoolean("pref_key_guardservice_switch", true);
                edit.putBoolean("pref_key_superpower_othersetting_state", true);
                edit.commit();
                c(false);
            }
            edit.putBoolean("pref_key_superpower_othersetting_state", true);
            edit.commit();
            Settings.System.putInt(this.f8065a.getContentResolver(), "POWER_SAVE_MODE_OPEN", 1);
        }
        f();
    }

    public boolean a() {
        return !o.m(this.f8065a) && this.f8066b.getBoolean("pref_key_superpower_othersetting_state", false);
    }

    public void c() {
        if (a()) {
            Log.w("SuperPowerSaveManager", "floatwindow policy restore state");
            d();
        }
    }

    public void d() {
        if (this.f8066b.getBoolean("pref_key_superpower_othersetting_state", false)) {
            Settings.System.putInt(this.f8065a.getContentResolver(), "POWER_SAVE_MODE_OPEN", 0);
            boolean e = e();
            boolean z = this.f8066b.getBoolean("pref_key_superpower_luckymoney_state", false);
            if (e != z) {
                b(z);
            }
            if (this.f8066b.getBoolean("pref_key_guardservice_switch", false)) {
                c(true);
            }
            SharedPreferences.Editor edit = this.f8066b.edit();
            edit.putBoolean("pref_key_superpower_luckymoney_state", false);
            edit.putBoolean("pref_key_guardservice_switch", false);
            edit.putBoolean("pref_key_superpower_othersetting_state", false);
            edit.commit();
        }
    }

    public String name() {
        return "other setting policy";
    }
}
