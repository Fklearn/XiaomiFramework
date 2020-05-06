package com.miui.gamebooster.gbservices;

import android.content.Context;
import android.util.Log;
import com.miui.gamebooster.c.a;
import com.miui.gamebooster.m.C0384o;
import com.miui.gamebooster.m.C0388t;

public class r extends m {

    /* renamed from: a  reason: collision with root package name */
    private boolean f4377a;

    /* renamed from: b  reason: collision with root package name */
    private Context f4378b;

    /* renamed from: c  reason: collision with root package name */
    private com.miui.gamebooster.service.r f4379c;

    public r(Context context, com.miui.gamebooster.service.r rVar) {
        this.f4378b = context;
        this.f4379c = rVar;
    }

    public void a() {
        if (this.f4377a) {
            Log.i("GameBoosterService", "mGWSDService...stop ");
            C0384o.b(this.f4378b.getContentResolver(), "gb_gwsd", 0, -2);
        }
    }

    public boolean b() {
        return C0388t.w();
    }

    public void c() {
        if (this.f4377a) {
            Log.i("GameBoosterService", "mGWSDService...start ");
            C0384o.b(this.f4378b.getContentResolver(), "gb_gwsd", 1, -2);
        }
    }

    public void d() {
        this.f4377a = a.j(false);
    }

    public int e() {
        return 10;
    }
}
