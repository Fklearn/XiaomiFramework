package com.miui.superpower.a;

import android.app.MiuiStatusBarManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;
import b.b.c.j.B;
import b.b.o.g.c;
import b.b.o.g.e;
import com.miui.earthquakewarning.Constants;
import com.miui.powercenter.utils.o;
import com.miui.superpower.b.k;
import com.miui.support.provider.f;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class l extends k {
    public l(Context context, SharedPreferences sharedPreferences) {
        super(context, sharedPreferences);
    }

    private void a(int i) {
        f.b(this.f8065a.getContentResolver(), "lock_screen_show_notifications", i, B.j());
    }

    private void b(int i) {
        Settings.Secure.putInt(this.f8065a.getContentResolver(), "disallow_key_menu", i);
    }

    private void b(boolean z) {
        Settings.Global.putInt(this.f8065a.getContentResolver(), "show_gesture_appswitch_feature", z ? 1 : 0);
    }

    private void c(boolean z) {
        c.a.a("android.provider.MiuiSettings$Global").b("putBoolean", new Class[]{ContentResolver.class, String.class, Boolean.TYPE}, this.f8065a.getContentResolver(), "force_fsg_nav_bar", Boolean.valueOf(z));
    }

    private boolean e() {
        int i;
        try {
            i = Settings.Secure.getInt(this.f8065a.getContentResolver(), "fw_fsgesture_support_superpower", 0);
        } catch (Exception e) {
            e.printStackTrace();
            i = 0;
        }
        return i == 1;
    }

    private boolean f() {
        try {
            return this.f8065a.getPackageManager().getApplicationInfo(Constants.SECURITY_ADD_PACKAGE, 128).metaData.getBoolean("is_support_superpower_fw");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean g() {
        Method method;
        if (!k.i(this.f8065a) || !h() || !e() || !f()) {
            return false;
        }
        Object obj = null;
        try {
            Object a2 = e.a(Class.forName("android.view.WindowManagerGlobal"), "getWindowManagerService", (Class<?>[]) null, new Object[0]);
            if (!(a2 == null || (method = a2.getClass().getMethod("getGestureStubListener", new Class[0])) == null)) {
                obj = method.invoke(a2, new Object[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj == null;
    }

    private boolean h() {
        int i;
        try {
            i = Settings.Secure.getInt(this.f8065a.getContentResolver(), "systemui_fsgesture_support_superpower", 0);
        } catch (Exception e) {
            e.printStackTrace();
            i = 0;
        }
        return i == 1;
    }

    private void i() {
        if (r()) {
            c(false);
        }
    }

    private void j() {
        if (n() > 0) {
            a(0);
        }
    }

    private void k() {
        if (p()) {
            MiuiStatusBarManager.setExpandableUnderKeyguardForUser(this.f8065a, false, B.j());
        }
    }

    private void l() {
        try {
            Object systemService = this.f8065a.getSystemService("statusbar");
            int intValue = ((Integer) e.a(Class.forName("android.view.View"), "STATUS_BAR_DISABLE_RECENT", Integer.TYPE)).intValue();
            int intValue2 = ((Integer) e.a(Class.forName("android.app.StatusBarManager"), "DISABLE_EXPAND", Integer.TYPE)).intValue();
            int intValue3 = ((Integer) e.a(Class.forName("android.app.StatusBarManager"), "DISABLE_NOTIFICATION_ICONS", Integer.TYPE)).intValue();
            int intValue4 = intValue | intValue3 | ((Integer) e.a(Class.forName("android.app.StatusBarManager"), "DISABLE_NOTIFICATION_ALERTS", Integer.TYPE)).intValue() | ((Integer) e.a(Class.forName("android.app.StatusBarManager"), "DISABLE_NOTIFICATION_TICKER", Integer.TYPE)).intValue();
            if (!k.a(this.f8065a)) {
                intValue4 |= intValue2;
            }
            e.a(systemService, "disable", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(intValue4));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        } catch (ClassNotFoundException e4) {
            e4.printStackTrace();
        } catch (NoSuchFieldException e5) {
            e5.printStackTrace();
        }
    }

    private void m() {
        try {
            Object systemService = this.f8065a.getSystemService("statusbar");
            int intValue = ((Integer) e.a(Class.forName("android.app.StatusBarManager"), "DISABLE_NONE", Integer.TYPE)).intValue();
            e.a(systemService, "disable", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(intValue));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        } catch (ClassNotFoundException e4) {
            e4.printStackTrace();
        } catch (NoSuchFieldException e5) {
            e5.printStackTrace();
        }
    }

    private int n() {
        return f.a(this.f8065a.getContentResolver(), "lock_screen_show_notifications", -1, B.j());
    }

    private boolean o() {
        return Settings.Global.getInt(this.f8065a.getContentResolver(), "show_gesture_appswitch_feature", 0) != 0;
    }

    private boolean p() {
        try {
            return ((Boolean) e.a((Class<?>) MiuiStatusBarManager.class, Boolean.TYPE, "isExpandableUnderKeyguardForUser", (Class<?>[]) new Class[]{Context.class, Integer.TYPE}, this.f8065a, Integer.valueOf(B.j()))).booleanValue();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
            return false;
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
            return false;
        }
    }

    private int q() {
        return Settings.Secure.getInt(this.f8065a.getContentResolver(), "disallow_key_menu", 0);
    }

    private boolean r() {
        c.a a2 = c.a.a("android.provider.MiuiSettings$Global");
        a2.b("getBoolean", new Class[]{ContentResolver.class, String.class}, this.f8065a.getContentResolver(), "force_fsg_nav_bar");
        return a2.a();
    }

    private void s() {
        if (this.f8066b.getBoolean("pref_key_superpower_gesturenavbar_originstate", false) != r()) {
            c(true);
        }
    }

    private void t() {
        int n = n();
        int i = this.f8066b.getInt("pref_key_superpower_keyguard_originnotifications", -1);
        if (n != i && i >= 0) {
            a(i);
        }
    }

    private void u() {
        boolean p = p();
        boolean z = this.f8066b.getBoolean("pref_key_superpower_keyguard_originexpand", false);
        if (p != z) {
            MiuiStatusBarManager.setExpandableUnderKeyguardForUser(this.f8065a, z, B.j());
        }
    }

    public void a(boolean z) {
        if (!this.f8066b.getBoolean("pref_key_superpower_systemui_state", false)) {
            boolean g = g();
            SharedPreferences.Editor edit = this.f8066b.edit();
            edit.putBoolean("pref_key_superpower_gesturenavbar_originstate", r());
            edit.putBoolean("pref_key_superpower_keyguard_originexpand", p());
            edit.putInt("pref_key_superpower_keyguard_originnotifications", n());
            edit.putBoolean("pref_key_superpower_appswitchfeature_state", o());
            edit.putBoolean("pref_key_superpower_fsgesture_origin_state", g);
            edit.putBoolean("pref_key_superpower_systemui_state", true);
            if (k.m(this.f8065a)) {
                edit.putInt("pref_key_superpower_physical_button", q());
            }
            edit.commit();
            if (!g) {
                i();
            } else if (r() && o()) {
                b(false);
            }
            k();
            j();
            l();
            if (k.m(this.f8065a)) {
                b(1);
            }
        }
    }

    public boolean a() {
        return !o.m(this.f8065a) && this.f8066b.getBoolean("pref_key_superpower_systemui_state", false);
    }

    public void b() {
        l();
    }

    public void c() {
        if (a()) {
            Log.w("SuperPowerSaveManager", "systemui policy restore state");
            d();
        }
    }

    public void d() {
        if (this.f8066b.getBoolean("pref_key_superpower_systemui_state", false)) {
            if (!this.f8066b.getBoolean("pref_key_superpower_fsgesture_origin_state", false)) {
                s();
            } else {
                boolean z = this.f8066b.getBoolean("pref_key_superpower_appswitchfeature_state", false);
                if (z != o()) {
                    b(z);
                }
            }
            u();
            t();
            m();
            if (k.m(this.f8065a)) {
                b(this.f8066b.getInt("pref_key_superpower_physical_button", 0));
            }
            this.f8066b.edit().putBoolean("pref_key_superpower_systemui_state", false).commit();
        }
    }

    public String name() {
        return "systemui policy";
    }
}
