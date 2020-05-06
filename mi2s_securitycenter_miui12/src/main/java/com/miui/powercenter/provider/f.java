package com.miui.powercenter.provider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.miui.networkassistant.config.Constants;
import com.miui.powercenter.b.a;
import com.miui.powercenter.utils.d;
import com.miui.powercenter.y;
import com.miui.superpower.b.k;
import com.miui.superpower.o;
import com.xiaomi.stat.MiStat;

class f extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    private int f7179a = 0;

    /* renamed from: b  reason: collision with root package name */
    private Boolean f7180b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ PowerSaveService f7181c;

    f(PowerSaveService powerSaveService) {
        this.f7181c = powerSaveService;
    }

    public void onReceive(Context context, Intent intent) {
        boolean z = false;
        if (Constants.System.ACTION_SCREEN_ON.equals(intent.getAction())) {
            if (this.f7181c.f) {
                this.f7181c.b();
            }
            if (this.f7181c.e) {
                this.f7181c.a();
                this.f7181c.e();
            }
            com.miui.superpower.b.f.a(context).a(false);
            this.f7181c.f7166d.e();
        } else {
            boolean z2 = true;
            if (Constants.System.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                this.f7181c.d();
                this.f7181c.c();
                com.miui.superpower.b.f.a(context).a(true);
                this.f7181c.f7166d.d();
            } else if (!"android.intent.action.BATTERY_CHANGED".equals(intent.getAction())) {
                return;
            } else {
                if (Build.VERSION.SDK_INT < 24 || !this.f7181c.isDeviceProtectedStorage()) {
                    int intExtra = intent.getIntExtra("status", -1);
                    int intExtra2 = (intent.getIntExtra(MiStat.Param.LEVEL, -1) * 100) / intent.getIntExtra("scale", -1);
                    if (!(intExtra == 2 || intExtra == 5)) {
                        z2 = false;
                    }
                    if (k.o(this.f7181c.getApplicationContext())) {
                        z = o.a(this.f7181c.getApplicationContext()).a(z2, intExtra2, this.f7179a);
                    }
                    com.miui.superpower.b.f.a(context).a(intExtra2, this.f7179a, z2);
                    if (intExtra2 > this.f7179a && z2 && y.a() && !z) {
                        this.f7181c.a(context, intExtra2);
                    }
                    this.f7181c.f7166d.a(intent);
                    if (this.f7180b == null) {
                        this.f7180b = Boolean.valueOf(z2);
                    }
                    this.f7179a = intExtra2;
                    d.a().a(z2, intExtra2);
                    return;
                }
                return;
            }
        }
        a.a(context, intent.getAction());
    }
}
