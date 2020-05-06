package com.miui.appmanager;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import com.miui.common.persistence.b;
import com.miui.securityscan.M;
import com.miui.securityscan.c.e;

public class i {

    /* renamed from: a  reason: collision with root package name */
    private Context f3675a;

    /* renamed from: b  reason: collision with root package name */
    private ContentResolver f3676b;

    /* renamed from: c  reason: collision with root package name */
    private e f3677c = e.a(this.f3675a, "data_config");

    public i(Context context) {
        this.f3675a = context;
        this.f3676b = context.getContentResolver();
    }

    public void a(boolean z) {
        b.b("am_ads_enable", z);
    }

    public boolean a() {
        if (AppManageUtils.a().compareTo(AppManageUtils.a(86400000)) <= 0) {
            AppManageUtils.a(false);
        }
        return e() && M.d() > 0 && !M.e().isEmpty() && AppManageUtils.a().compareTo(AppManageUtils.a(86400000)) <= 0 && !AppManageUtils.c();
    }

    public void b(boolean z) {
        Settings.System.putInt(this.f3676b, "com.miui.thirdappassistant.switch.key_can_use", z ? 1 : 0);
    }

    public boolean b() {
        if (this.f3677c.a("am_ads_enable")) {
            a(this.f3677c.a("am_ads_enable", true));
            this.f3677c.b("am_ads_enable");
        }
        return b.a("am_ads_enable", true);
    }

    public void c(boolean z) {
        Settings.Secure.putInt(this.f3676b, "am_show_system_apps", z ? 1 : 0);
    }

    public boolean c() {
        return Settings.System.getInt(this.f3676b, "com.miui.thirdappassistant.switch.key_can_use", 1) == 1;
    }

    public void d(boolean z) {
        Settings.Secure.putInt(this.f3676b, "am_update_app_notify", z ? 1 : 0);
    }

    public boolean d() {
        return Settings.Secure.getInt(this.f3676b, "am_show_system_apps", 0) == 1;
    }

    public boolean e() {
        return Settings.Secure.getInt(this.f3676b, "am_update_app_notify", 1) == 1;
    }
}
