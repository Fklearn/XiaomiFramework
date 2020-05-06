package com.miui.applicationlock.c;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.j.j;
import b.b.o.g.c;
import com.miui.applicationlock.a.i;
import com.miui.common.persistence.b;
import com.miui.gamebooster.m.C0384o;
import java.util.HashSet;
import java.util.Iterator;
import miui.security.SecurityManager;

/* renamed from: com.miui.applicationlock.c.c  reason: case insensitive filesystem */
public class C0259c {

    /* renamed from: a  reason: collision with root package name */
    private static volatile C0259c f3296a;

    /* renamed from: b  reason: collision with root package name */
    private final String f3297b = "access_control_lock_enabled";

    /* renamed from: c  reason: collision with root package name */
    private ContentResolver f3298c;

    /* renamed from: d  reason: collision with root package name */
    private Context f3299d;
    /* access modifiers changed from: private */
    public HashSet<String> e = new HashSet<>();
    private Handler f = new C0258b(this, Looper.getMainLooper());

    private C0259c(Context context) {
        this.f3299d = context.getApplicationContext();
        this.f3298c = this.f3299d.getContentResolver();
    }

    public static void a(ContentResolver contentResolver, int i) {
        C0384o.b(contentResolver, "fod_auth_fingerprint", 0, i);
    }

    public static void a(Context context) {
        SecurityManager securityManager = (SecurityManager) context.getSystemService("security");
        Iterator it = o.f3317a.iterator();
        while (it.hasNext()) {
            securityManager.setApplicationAccessControlEnabled((String) it.next(), false);
        }
    }

    public static C0259c b(Context context) {
        if (f3296a == null) {
            synchronized (C0259c.class) {
                if (f3296a == null) {
                    f3296a = new C0259c(context);
                }
            }
        }
        return f3296a;
    }

    public static boolean b(ContentResolver contentResolver, int i) {
        if (C0384o.a(contentResolver, "fod_quick_open", 1, 0) == 1) {
            return o.c(C0384o.a(contentResolver, "fod_auth_fingerprint", 0, i), i);
        }
        return false;
    }

    public int a() {
        return Settings.Secure.getInt(this.f3298c, "access_control_lock_mode", 1);
    }

    public void a(int i) {
        Settings.Secure.putInt(this.f3298c, "access_control_lock_mode", i);
    }

    public void a(String str) {
        Settings.Secure.putString(this.f3298c, "app_lock_add_account_md5", !TextUtils.isEmpty(str) ? j.d(str.getBytes()) : null);
    }

    public void a(boolean z) {
        Settings.Secure.putInt(this.f3298c, "access_control_lock_enabled", z ? 1 : 0);
    }

    public String b() {
        String string = Settings.Secure.getString(this.f3298c, "app_lock_bind_xiaomi_account");
        if (!TextUtils.isEmpty(string)) {
            Settings.Secure.putString(this.f3298c, "app_lock_add_account_md5", j.d(string.getBytes()));
            Settings.Secure.putString(this.f3298c, "app_lock_bind_xiaomi_account", (String) null);
        }
        return Settings.Secure.getString(this.f3298c, "app_lock_add_account_md5");
    }

    public void b(String str) {
        if (str != null) {
            this.e.add(str);
            Message obtainMessage = this.f.obtainMessage(1);
            obtainMessage.obj = str;
            this.f.sendMessageDelayed(obtainMessage, 300);
        }
    }

    public void b(boolean z) {
        Settings.Secure.putInt(this.f3298c, "access_control_lock_convenient", z ? 1 : 0);
    }

    public void c(boolean z) {
        Settings.Secure.putInt(this.f3298c, i.f3251b, z ? 1 : 0);
    }

    public boolean c() {
        return b.a("privacy_password_bind_xiaomi_account_remind", 0) == 0;
    }

    public boolean c(String str) {
        return this.e.contains(str);
    }

    public void d(boolean z) {
        Settings.Secure.putInt(this.f3298c, i.f3250a, z ? 2 : 1);
    }

    public boolean d() {
        try {
            c.a a2 = c.a.a("miui.securitycenter.applicationlock.ChooserLockSettingsHelperWrapper");
            a2.a(new Class[]{Context.class}, this.f3299d);
            a2.a("isACLockEnabled", new Class[0], new Object[0]);
            return a2.a();
        } catch (Exception e2) {
            Log.d("AppLockManager", "havePattern exception: ", e2);
            return false;
        }
    }

    public void e(boolean z) {
        Settings.Secure.putInt(this.f3298c, "access_control_lock_enabled", z ? -1 : 1);
    }

    public boolean e() {
        return Settings.Secure.getInt(this.f3298c, "access_control_lock_enabled", -1) == 1;
    }

    public void f(boolean z) {
        Settings.Secure.putInt(this.f3298c, "access_control_lock_all", z ? 1 : 0);
    }

    public boolean f() {
        return Settings.Secure.getInt(this.f3298c, "access_control_lock_all", 0) == 1;
    }

    public void g(boolean z) {
        Settings.Secure.putInt(this.f3298c, "access_control_mask_notification", z ? 1 : 0);
    }

    public boolean g() {
        return Settings.Secure.getInt(this.f3298c, "access_control_lock_convenient", 0) == 1;
    }

    public void h(boolean z) {
        b.b("privacy_password_bind_xiaomi_account_remind", z ? 1 : 0);
    }

    public boolean h() {
        return Settings.Secure.getInt(this.f3298c, i.f3251b, 0) == 1;
    }

    public boolean i() {
        return Settings.Secure.getInt(this.f3298c, i.f3250a, 1) == 2;
    }

    public boolean j() {
        return Settings.Secure.getInt(this.f3298c, "access_control_lock_enabled", -1) == -1;
    }
}
