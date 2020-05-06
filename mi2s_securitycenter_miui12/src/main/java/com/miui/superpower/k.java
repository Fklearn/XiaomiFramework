package com.miui.superpower;

import android.content.ComponentName;
import android.content.pm.ResolveInfo;
import android.util.Log;
import com.miui.earthquakewarning.Constants;
import com.miui.powercenter.utils.o;
import com.miui.superpower.a.d;
import com.miui.superpower.b.a;
import com.miui.superpower.b.f;
import com.miui.superpower.b.h;

class k implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f8108a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ boolean f8109b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ o f8110c;

    k(o oVar, boolean z, boolean z2) {
        this.f8110c = oVar;
        this.f8108a = z;
        this.f8109b = z2;
    }

    public void run() {
        StringBuilder sb = new StringBuilder();
        sb.append("bSuperPower(");
        sb.append(this.f8108a);
        sb.append(")");
        sb.append("-fromuser(");
        sb.append(this.f8109b);
        sb.append(")");
        sb.append("-powerpercent(");
        sb.append(this.f8110c.i);
        sb.append(")");
        sb.append("-userenterstate(");
        sb.append(this.f8110c.n);
        sb.append(")");
        sb.append("-userleavestate(");
        sb.append(this.f8110c.o);
        sb.append(")");
        if (this.f8108a && !this.f8110c.j.get()) {
            Log.w("SuperPowerSaveManager", "switchSuperPower enter super power : " + sb.toString());
            o oVar = this.f8110c;
            ResolveInfo unused = oVar.h = a.a(oVar.f8135c);
            this.f8110c.a(1);
            if (a.b() != null) {
                Log.w("SuperPowerSaveManager", "split screen mode exit");
                a.a();
            }
            SuperPowerProgressActivity.a(this.f8110c.f8135c);
            this.f8110c.j();
            for (d dVar : this.f8110c.k) {
                Log.w("SuperPowerSaveManager", "enter " + dVar.name());
                try {
                    dVar.a(this.f8109b);
                } catch (Exception e) {
                    Log.e("SuperPowerSaveManager", "enter superpower excepiton : " + e);
                }
                Log.w("SuperPowerSaveManager", "leave " + dVar.name());
            }
            try {
                this.f8110c.f8135c.getPackageManager().setComponentEnabledSetting(new ComponentName(Constants.SECURITY_ADD_PACKAGE, "com.miui.superpower.SuperPowerLauncherActivity"), 1, 1);
            } catch (Exception e2) {
                Log.e("SuperPowerSaveManager", "enter setaddhomeenable exception : " + e2);
            }
            this.f8110c.h();
            if (this.f8109b && this.f8110c.i >= 50) {
                boolean unused2 = this.f8110c.n = true;
                boolean unused3 = this.f8110c.o = true;
                this.f8110c.q.edit().putBoolean("pref_key_superpower_user_entersuperpower", true).putBoolean("pref_key_superpower_user_leavesuperpower", true).commit();
            }
            this.f8110c.j.set(true);
            h.d(o.e(this.f8110c.f8135c));
        } else if (this.f8108a || !this.f8110c.j.get()) {
            sb.append("-mIsSuperSaveMode(");
            sb.append(this.f8110c.j.get());
            sb.append(")");
            Log.w("SuperPowerSaveManager", "switchSuperPower state error : " + sb.toString());
            return;
        } else {
            Log.w("SuperPowerSaveManager", "switchSuperPower leave super power : " + sb.toString());
            this.f8110c.f();
            this.f8110c.a(0);
            try {
                this.f8110c.f8135c.getPackageManager().setComponentEnabledSetting(new ComponentName(Constants.SECURITY_ADD_PACKAGE, "com.miui.superpower.SuperPowerLauncherActivity"), 2, 1);
            } catch (Exception e3) {
                Log.e("SuperPowerSaveManager", "enter setaddhomedisable exception : " + e3);
            }
            for (d dVar2 : this.f8110c.k) {
                Log.w("SuperPowerSaveManager", "enter " + dVar2.name());
                try {
                    dVar2.d();
                } catch (Exception e4) {
                    Log.e("SuperPowerSaveManager", "leave superpower excepiton : " + e4);
                }
                Log.w("SuperPowerSaveManager", "leave " + dVar2.name());
            }
            this.f8110c.e.postDelayed(new j(this), 1000);
            if (this.f8109b && this.f8110c.i <= 5) {
                boolean unused4 = this.f8110c.n = true;
                boolean unused5 = this.f8110c.o = true;
                this.f8110c.q.edit().putBoolean("pref_key_superpower_user_entersuperpower", true).putBoolean("pref_key_superpower_user_leavesuperpower", true).commit();
            }
            this.f8110c.j.set(false);
            h.c(o.e(this.f8110c.f8135c));
        }
        f.a(this.f8110c.f8135c).a();
    }
}
