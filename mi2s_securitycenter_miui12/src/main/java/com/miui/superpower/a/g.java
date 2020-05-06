package com.miui.superpower.a;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.miui.gamebooster.service.NotificationListener;
import com.miui.luckymoney.utils.SettingsUtil;
import com.miui.powercenter.utils.o;
import com.miui.superpower.notification.f;

public class g extends k {

    /* renamed from: c  reason: collision with root package name */
    private com.miui.superpower.statusbar.g f8054c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public TelephonyManager f8055d;
    /* access modifiers changed from: private */
    public ContentResolver e;
    private f f;
    private a g;
    /* access modifiers changed from: private */
    public boolean h = false;
    /* access modifiers changed from: private */
    public boolean i = false;

    private class a extends HandlerThread {

        /* renamed from: a  reason: collision with root package name */
        private b f8056a;

        /* renamed from: b  reason: collision with root package name */
        private c f8057b;

        private a() {
            super("NewNotificationPolicy");
        }

        /* access modifiers changed from: protected */
        public void onLooperPrepared() {
            super.onLooperPrepared();
            this.f8056a = new b();
            this.f8057b = new c();
            g.this.f8055d.listen(this.f8056a, 32);
            g.this.e.registerContentObserver(Settings.Secure.getUriFor("key_is_in_miui_sos_mode"), false, this.f8057b);
        }

        public boolean quitSafely() {
            g.this.f8055d.listen(this.f8056a, 0);
            g.this.e.unregisterContentObserver(this.f8057b);
            return super.quitSafely();
        }
    }

    private class b extends PhoneStateListener {
        private b() {
        }

        public void onCallStateChanged(int i, String str) {
            super.onCallStateChanged(i, str);
            boolean unused = g.this.i = i != 0;
            g.this.h();
        }
    }

    private class c extends ContentObserver {
        private c() {
            super(new Handler());
        }

        public void onChange(boolean z) {
            g gVar = g.this;
            boolean z2 = false;
            if (Settings.Secure.getInt(gVar.e, "key_is_in_miui_sos_mode", 0) == 1) {
                z2 = true;
            }
            boolean unused = gVar.h = z2;
            g.this.h();
        }
    }

    public g(Context context, SharedPreferences sharedPreferences) {
        super(context, sharedPreferences);
        this.f8054c = com.miui.superpower.statusbar.g.a(context);
        this.f8055d = (TelephonyManager) context.getSystemService("phone");
        this.e = context.getContentResolver();
        this.f = new f(context);
    }

    private boolean e() {
        this.h = Settings.Secure.getInt(this.e, "key_is_in_miui_sos_mode", 0) == 1;
        this.i = this.f8055d.getCallState() != 0;
        return !this.h && !this.i;
    }

    private void f() {
        if (this.g == null) {
            this.g = new a();
            this.g.start();
            this.g.getLooper();
        }
    }

    private void g() {
        a aVar = this.g;
        if (aVar != null) {
            aVar.quitSafely();
            this.g = null;
        }
    }

    /* access modifiers changed from: private */
    public void h() {
        this.f8054c.b(!this.h && !this.i);
    }

    public void a(boolean z) {
        this.f8054c.a(e());
        SettingsUtil.enableNotificationListener(this.f8065a, NotificationListener.class);
        this.f8066b.edit().putBoolean("pref_key_superpower_notification_state", true).commit();
        this.f.a();
        f();
    }

    public boolean a() {
        return !o.m(this.f8065a) && this.f8066b.getBoolean("pref_key_superpower_notification_state", false);
    }

    public void b() {
        super.b();
        this.f8054c.a(e());
        if (!this.f8066b.getBoolean("pref_key_superpower_notification_state", false)) {
            SettingsUtil.enableNotificationListener(this.f8065a, NotificationListener.class);
            this.f8066b.edit().putBoolean("pref_key_superpower_notification_state", true).commit();
        }
        this.f.a();
        f();
    }

    public void c() {
        if (a()) {
            Log.w("SuperPowerSaveManager", "notification policy restore state");
            SettingsUtil.closeNotificationListener(this.f8065a, NotificationListener.class);
            this.f8066b.edit().putBoolean("pref_key_superpower_notification_state", false).commit();
        }
    }

    public void d() {
        this.f8054c.a();
        SettingsUtil.closeNotificationListener(this.f8065a, NotificationListener.class);
        this.f8066b.edit().putBoolean("pref_key_superpower_notification_state", false).commit();
        g();
    }

    public String name() {
        return "new notification policy";
    }
}
