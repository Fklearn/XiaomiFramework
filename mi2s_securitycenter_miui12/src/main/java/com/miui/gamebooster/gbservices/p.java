package com.miui.gamebooster.gbservices;

import android.content.Context;
import android.util.Log;
import com.miui.gamebooster.c.a;
import com.miui.gamebooster.m.C0393y;
import com.miui.gamebooster.m.D;
import com.miui.gamebooster.service.r;

public class p extends m {

    /* renamed from: a  reason: collision with root package name */
    private Context f4371a;

    /* renamed from: b  reason: collision with root package name */
    private r f4372b;

    public p(Context context, r rVar) {
        this.f4371a = context;
        this.f4372b = rVar;
    }

    public void a() {
        if (a.f(false) || C0393y.c(this.f4371a)) {
            Log.i("GameBoosterService", "mDataBooster...stop ");
            D.a(this.f4371a, false);
            a.E(false);
        }
    }

    public boolean b() {
        return true;
    }

    public void c() {
    }

    public void d() {
    }

    public int e() {
        return 4;
    }
}
