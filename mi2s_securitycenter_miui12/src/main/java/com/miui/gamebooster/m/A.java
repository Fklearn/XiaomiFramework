package com.miui.gamebooster.m;

import android.content.Context;
import android.widget.Toast;
import com.miui.securitycenter.R;
import miui.util.HardwareInfo;

class A implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f4446a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Context f4447b;

    A(int i, Context context) {
        this.f4446a = i;
        this.f4447b = context;
    }

    public void run() {
        int abs = Math.abs(this.f4446a - ((int) (Math.abs(HardwareInfo.getFreeMemory()) / 1048576)));
        Context context = this.f4447b;
        Toast.makeText(context, context.getString(R.string.release_meminfo, new Object[]{Integer.valueOf(Math.abs(abs))}), 0).show();
    }
}
