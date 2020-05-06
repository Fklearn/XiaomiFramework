package com.miui.superpower.a;

import android.app.UiModeManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import b.b.c.h.f;
import b.b.c.h.l;
import b.b.c.j.B;
import b.b.o.a.b;
import b.b.o.g.e;
import com.miui.powercenter.d.a;
import com.miui.powercenter.d.d;
import com.miui.powercenter.d.g;
import com.miui.powercenter.utils.n;
import com.miui.powercenter.utils.o;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class j extends k {

    /* renamed from: c  reason: collision with root package name */
    private Object f8063c;

    /* renamed from: d  reason: collision with root package name */
    private List<d> f8064d = new ArrayList();

    public j(Context context, SharedPreferences sharedPreferences) {
        super(context, sharedPreferences);
        t();
    }

    private float a(int i) {
        try {
            Object s = s();
            if (s == null) {
                return 0.0f;
            }
            return ((Float) e.a(s, Float.TYPE, "getAnimationScale", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(i))).floatValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0f;
        }
    }

    private void a(int i, float f) {
        try {
            Object s = s();
            if (s != null) {
                e.a(s, (Class) null, "setAnimationScale", (Class<?>[]) new Class[]{Integer.TYPE, Float.TYPE}, Integer.valueOf(i), Float.valueOf(f));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void b(int i) {
        if (i != n.a(this.f8065a).c() && i != Integer.MIN_VALUE) {
            n.a(this.f8065a).a(i);
        }
    }

    private void b(boolean z) {
        if (z != i()) {
            Settings.Secure.putInt(this.f8065a.getContentResolver(), "auto_download", z ? 1 : 0);
        }
    }

    private void c(int i) {
        if (i != n.a(this.f8065a).d() && i != Integer.MIN_VALUE) {
            n.a(this.f8065a).b(i);
        }
    }

    private void c(boolean z) {
        if (z != j()) {
            Settings.Secure.putInt(this.f8065a.getContentResolver(), "auto_update", z ? 1 : 0);
        }
    }

    private void d(int i) {
        if (i != Integer.MIN_VALUE || i != m()) {
            Class<Settings.Secure> cls = Settings.Secure.class;
            try {
                e.a((Class<?>) cls, "putIntForUser", (Class<?>[]) new Class[]{ContentResolver.class, String.class, Integer.TYPE, Integer.TYPE}, this.f8065a.getContentResolver(), "face_unlcok_apply_for_lock", Integer.valueOf(i), Integer.valueOf(B.c()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void d(boolean z) {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null && z != k()) {
            if (z) {
                defaultAdapter.enable();
            } else {
                defaultAdapter.disable();
            }
        }
    }

    private void e(int i) {
        if (i != o.g(this.f8065a)) {
            o.a(this.f8065a, i);
        }
    }

    private void e(boolean z) {
        if (l() != z) {
            Settings.System.putInt(this.f8065a.getContentResolver(), "dtmf_tone", z ? 1 : 0);
        }
    }

    private boolean e() {
        int i;
        try {
            i = Settings.Secure.getInt(this.f8065a.getContentResolver(), "faceunlock_support_superpower", 0);
        } catch (Exception e) {
            e.printStackTrace();
            i = 0;
        }
        return i == 1;
    }

    private void f() {
        try {
            int intValue = ((Integer) e.a(Class.forName("miui.app.ToggleManager"), "TOGGLE_TORCH", Integer.TYPE)).intValue();
            Object a2 = e.a(Class.forName("miui.app.ToggleManager"), "createInstance", (Class<?>[]) new Class[]{Context.class}, this.f8065a);
            e.a(a2, Void.TYPE, "updateTorchToggle", (Class<?>[]) new Class[0], (Object[]) null);
            if (((Boolean) e.a(Class.forName("miui.app.ToggleManager"), Boolean.TYPE, "getStatus", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(intValue))).booleanValue()) {
                e.a(a2, "performToggle", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(intValue));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void f(int i) {
        if (Build.VERSION.SDK_INT > 27 && i != p()) {
            try {
                ((UiModeManager) this.f8065a.getSystemService("uimode")).setNightMode(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void f(boolean z) {
        if (n() != z) {
            Settings.System.putInt(this.f8065a.getContentResolver(), "handy_mode_state", z ? 1 : 0);
        }
    }

    private void g() {
        l.a(this.f8065a, false);
        d(false);
        e(0);
        i(0);
        k(false);
        j(false);
        h(15);
        c(0);
        w();
        f();
        if (!this.f8066b.getBoolean("sp_faceunlock_supported", false)) {
            d(0);
        }
        f(2);
        b(false);
        c(false);
        h(false);
        f(false);
        if (a(0) != 0.0f) {
            a(0, 0.0f);
        }
        if (a(1) != 0.0f) {
            a(1, 0.0f);
        }
        if (q() == 1) {
            g(0);
        }
        for (d b2 : this.f8064d) {
            b2.b(this.f8065a);
        }
    }

    private void g(int i) {
        Settings.System.putInt(this.f8065a.getContentResolver(), "notification_light_pulse", i);
    }

    private void g(boolean z) {
        if (z != o()) {
            String str = z ? "enable" : "disable";
            try {
                NfcAdapter defaultAdapter = NfcAdapter.getDefaultAdapter(this.f8065a);
                if (defaultAdapter != null) {
                    Method declaredMethod = Class.forName(defaultAdapter.getClass().getName()).getDeclaredMethod(str, new Class[0]);
                    declaredMethod.setAccessible(true);
                    declaredMethod.invoke(defaultAdapter, new Object[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void h() {
        l(this.f8066b.getBoolean("SP_WIFI_AP_ENABLED", false));
        d(this.f8066b.getBoolean("SP_BLUETOOTH_ENABLED", false));
        e(this.f8066b.getInt("SP_GPS_MODE", 0));
        i(this.f8066b.getInt("SP_SYNC_ENABLED", Integer.MIN_VALUE));
        k(this.f8066b.getBoolean("SP_VIBRATE_ENABLED", false));
        j(this.f8066b.getBoolean("SP_TOUCH_VIBRATION_ENABLED", false));
        if (this.f8066b.getBoolean("SP_DIALOG_RING_ENABLED", false)) {
            e(this.f8066b.getBoolean("SP_DIALOG_RING_ENABLED", false));
        }
        if (this.f8066b.getBoolean("SP_TOUCH_RING_ENABLED", false)) {
            i(this.f8066b.getBoolean("SP_TOUCH_RING_ENABLED", false));
        }
        h(this.f8066b.getInt("SP_SLEEP_SECONDS", Integer.MIN_VALUE));
        c(this.f8066b.getInt("SP_BRIGHTNESS_MODE", Integer.MIN_VALUE));
        b(this.f8066b.getInt("SP_BRIGHTNESS", Integer.MIN_VALUE));
        if (!this.f8066b.getBoolean("sp_faceunlock_supported", false)) {
            d(this.f8066b.getInt("SP_FACE_UNLOCK_ENABLED", Integer.MIN_VALUE));
        }
        f(this.f8066b.getInt("SP_NIGHT_MODE_ENABLED", 1));
        if (this.f8066b.getBoolean("SP_NFC_ENABLED", false)) {
            g(true);
            SharedPreferences.Editor edit = this.f8066b.edit();
            edit.putBoolean("SP_NFC_ENABLED", false);
            edit.commit();
        }
        c(this.f8066b.getBoolean("SP_AUTO_MODE_ENABLED", true));
        b(this.f8066b.getBoolean("SP_AUTO_DOWNLOAD_ENABLED", true));
        h(this.f8066b.getBoolean("sp_touchassistant_enabled", false));
        f(this.f8066b.getBoolean("sp_handy_mode_enabled", false));
        float f = this.f8066b.getFloat("SP_WINDOW_ANIMATION_SCALE", 1.0f);
        if (a(0) != f) {
            a(0, f);
        }
        float f2 = this.f8066b.getFloat("SP_TRANSITION_ANIMATION_SCALE", 1.0f);
        if (a(1) != f2) {
            a(1, f2);
        }
        int i = this.f8066b.getInt("sp_notification_light_pulse", -1);
        if (!(i == -1 || i == q())) {
            g(i);
        }
        for (d a2 : this.f8064d) {
            a2.a(this.f8065a);
        }
    }

    private void h(int i) {
        if (i != n.a(this.f8065a).g() && i != Integer.MIN_VALUE) {
            n.a(this.f8065a).d(i);
        }
    }

    private void h(boolean z) {
        if (r() != z) {
            Settings.System.putInt(this.f8065a.getContentResolver(), "touch_assistant_enabled", z ? 1 : 0);
            if (z) {
                try {
                    this.f8065a.startService(new Intent("com.miui.touchassistant.SHOW_FLOATING_WINDOW").setClassName("com.miui.touchassistant", "com.miui.touchassistant.CoreService"));
                } catch (Exception e) {
                    Log.e("SuperPowerSaveManager", "Error while enable touchassistant" + e);
                }
            }
        }
    }

    private void i(int i) {
        if (u() != Integer.MIN_VALUE && i != Integer.MIN_VALUE && u() != i) {
            try {
                int intValue = ((Integer) e.a(Class.forName("miui.app.ToggleManager"), "TOGGLE_SYNC", Integer.TYPE)).intValue();
                e.a(e.a(Class.forName("miui.app.ToggleManager"), "createInstance", (Class<?>[]) new Class[]{Context.class}, this.f8065a), "performToggle", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(intValue));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void i(boolean z) {
        if (n.a(this.f8065a).i() != z) {
            n.a(this.f8065a).e(z);
        }
    }

    private boolean i() {
        return Settings.Secure.getInt(this.f8065a.getContentResolver(), "auto_download", 1) == 1;
    }

    private void j(boolean z) {
        if (n.a(this.f8065a).j() != z) {
            n.a(this.f8065a).f(z);
        }
    }

    private boolean j() {
        return Settings.Secure.getInt(this.f8065a.getContentResolver(), "auto_update", 1) == 1;
    }

    private void k(boolean z) {
        if (n.a(this.f8065a).k() != z) {
            n.a(this.f8065a).g(z);
        }
    }

    private boolean k() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        return defaultAdapter != null && defaultAdapter.isEnabled();
    }

    private void l(boolean z) {
        if (z != f.k(this.f8065a)) {
            try {
                int intValue = ((Integer) e.a(Class.forName("miui.app.ToggleManager"), "TOGGLE_WIFI_AP", Integer.TYPE)).intValue();
                e.a(e.a(Class.forName("miui.app.ToggleManager"), "createInstance", (Class<?>[]) new Class[]{Context.class}, this.f8065a), "performToggle", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(intValue));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean l() {
        return Settings.System.getInt(this.f8065a.getContentResolver(), "dtmf_tone", -1) == 1;
    }

    private int m() {
        try {
            return ((Integer) e.a((Class<?>) Settings.Secure.class, "getIntForUser", (Class<?>[]) new Class[]{ContentResolver.class, String.class, Integer.TYPE, Integer.TYPE}, this.f8065a.getContentResolver(), "face_unlcok_apply_for_lock", 0, Integer.valueOf(B.c()))).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private boolean n() {
        return Settings.System.getInt(this.f8065a.getContentResolver(), "handy_mode_state", 0) == 1;
    }

    private boolean o() {
        try {
            NfcAdapter defaultAdapter = NfcAdapter.getDefaultAdapter(this.f8065a);
            return defaultAdapter != null && defaultAdapter.isEnabled();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private int p() {
        try {
            return ((UiModeManager) this.f8065a.getSystemService("uimode")).getNightMode();
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    private int q() {
        return Settings.System.getInt(this.f8065a.getContentResolver(), "notification_light_pulse", 0);
    }

    private boolean r() {
        return Settings.System.getInt(this.f8065a.getContentResolver(), "touch_assistant_enabled", 0) == 1;
    }

    private Object s() {
        if (this.f8063c == null) {
            try {
                IBinder a2 = b.a("window");
                this.f8063c = e.a(Class.forName("android.view.IWindowManager$Stub"), "asInterface", (Class<?>[]) new Class[]{IBinder.class}, a2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.f8063c;
    }

    private void t() {
        this.f8064d.add(new com.miui.powercenter.d.b());
        this.f8064d.add(new com.miui.powercenter.d.e());
        this.f8064d.add(new a());
        this.f8064d.add(new com.miui.powercenter.d.f());
        this.f8064d.add(new g());
    }

    private int u() {
        try {
            return ((Boolean) e.a(e.a(Class.forName("miui.app.ToggleManager"), "createInstance", (Class<?>[]) new Class[]{Context.class}, this.f8065a), Boolean.class, "isSyncOn", (Class<?>[]) new Class[0], new Object[0])).booleanValue() ? 1 : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return Integer.MIN_VALUE;
        }
    }

    private void v() {
        boolean e = e();
        SharedPreferences.Editor edit = this.f8066b.edit();
        edit.putBoolean("SP_WIFI_AP_ENABLED", f.k(this.f8065a));
        edit.putBoolean("SP_BLUETOOTH_ENABLED", k());
        edit.putInt("SP_GPS_MODE", o.g(this.f8065a));
        edit.putInt("SP_SYNC_ENABLED", u());
        edit.putBoolean("SP_VIBRATE_ENABLED", n.a(this.f8065a).k());
        edit.putBoolean("SP_TOUCH_VIBRATION_ENABLED", n.a(this.f8065a).j());
        edit.putBoolean("SP_DIALOG_RING_ENABLED", l());
        edit.putBoolean("SP_TOUCH_RING_ENABLED", n.a(this.f8065a).i());
        edit.putInt("SP_SLEEP_SECONDS", n.a(this.f8065a).g());
        edit.putInt("SP_BRIGHTNESS_MODE", n.a(this.f8065a).d());
        edit.putInt("SP_BRIGHTNESS", n.a(this.f8065a).c());
        if (!e) {
            edit.putInt("SP_FACE_UNLOCK_ENABLED", m());
        }
        edit.putInt("SP_NIGHT_MODE_ENABLED", p());
        edit.putBoolean("SP_AUTO_MODE_ENABLED", j());
        edit.putBoolean("SP_AUTO_DOWNLOAD_ENABLED", i());
        edit.putFloat("SP_WINDOW_ANIMATION_SCALE", a(0));
        edit.putFloat("SP_TRANSITION_ANIMATION_SCALE", a(1));
        edit.putInt("sp_notification_light_pulse", q());
        edit.putBoolean("sp_touchassistant_enabled", r());
        edit.putBoolean("sp_handy_mode_enabled", n());
        edit.putBoolean("sp_faceunlock_supported", e);
        edit.commit();
    }

    private void w() {
        int e = (int) (((double) n.a(this.f8065a).e()) * 0.3d);
        if (e < n.a(this.f8065a).c()) {
            b(e);
        }
    }

    public void a(boolean z) {
        if (!this.f8066b.getBoolean("PREF_KEY_SUPERPOWER_SETTING_DISABLE_STATE", false)) {
            v();
            this.f8066b.edit().putBoolean("PREF_KEY_SUPERPOWER_SETTING_DISABLE_STATE", true).commit();
            g();
        }
    }

    public boolean a() {
        return !o.m(this.f8065a) && this.f8066b.getBoolean("PREF_KEY_SUPERPOWER_SETTING_DISABLE_STATE", false);
    }

    public void c() {
        Log.w("SuperPowerSaveManager", "resotre settings state");
        d();
    }

    public void d() {
        if (this.f8066b.getBoolean("PREF_KEY_SUPERPOWER_SETTING_DISABLE_STATE", false)) {
            h();
            this.f8066b.edit().putBoolean("PREF_KEY_SUPERPOWER_SETTING_DISABLE_STATE", false).commit();
        }
    }

    public String name() {
        return "settiing policy";
    }
}
