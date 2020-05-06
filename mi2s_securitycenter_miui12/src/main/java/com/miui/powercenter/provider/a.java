package com.miui.powercenter.provider;

import android.content.Context;
import b.b.c.j.g;
import com.miui.powercenter.quickoptimize.z;
import java.util.List;

class a implements z.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f7167a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ AlarmReceiver f7168b;

    a(AlarmReceiver alarmReceiver, Context context) {
        this.f7168b = alarmReceiver;
        this.f7167a = context;
    }

    public void a(List<String> list) {
        if (!list.isEmpty()) {
            z.a(list, g.a(this.f7167a));
            if (g.a(this.f7167a) == 0) {
                List<String> a2 = z.a(this.f7167a, list);
                if (!a2.isEmpty()) {
                    z.a(a2, 999);
                }
            }
        }
    }
}
