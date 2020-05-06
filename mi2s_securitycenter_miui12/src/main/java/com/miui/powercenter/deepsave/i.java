package com.miui.powercenter.deepsave;

import android.content.Context;
import android.content.SharedPreferences;
import com.miui.powercenter.utils.p;

public class i {

    /* renamed from: a  reason: collision with root package name */
    private SharedPreferences f7057a;

    /* renamed from: b  reason: collision with root package name */
    private p f7058b;

    public i(Context context) {
        this.f7057a = context.getSharedPreferences("pc_sp_data_config", 0);
        this.f7058b = new p(context, "layout_data");
    }

    public void a(String str) {
        this.f7058b.a(str);
    }

    public void a(boolean z) {
        this.f7057a.edit().putBoolean("init_success", z).apply();
    }

    public boolean a() {
        return this.f7057a.getBoolean("init_success", false);
    }

    public String b() {
        return this.f7058b.a();
    }
}
