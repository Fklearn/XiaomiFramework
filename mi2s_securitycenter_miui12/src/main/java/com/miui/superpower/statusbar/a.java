package com.miui.superpower.statusbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public abstract class a extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    private Context f8153a;

    /* renamed from: b  reason: collision with root package name */
    private boolean f8154b = false;

    /* renamed from: c  reason: collision with root package name */
    public IntentFilter f8155c = new IntentFilter();

    public a(Context context) {
        this.f8153a = context;
    }

    public Intent a() {
        if (this.f8154b || this.f8153a == null) {
            return null;
        }
        this.f8154b = true;
        this.f8155c.setPriority(1000);
        return this.f8153a.registerReceiver(this, this.f8155c);
    }

    public void b() {
        Context context;
        if (this.f8154b && (context = this.f8153a) != null) {
            this.f8154b = false;
            context.unregisterReceiver(this);
        }
    }
}
