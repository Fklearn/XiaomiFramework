package com.miui.securityscan.cards;

import android.content.Context;
import android.os.AsyncTask;
import com.miui.powercenter.batteryhistory.C0501e;
import com.miui.powercenter.batteryhistory.C0514s;
import com.miui.powercenter.batteryhistory.C0520y;
import com.miui.powercenter.batteryhistory.aa;
import com.miui.powercenter.utils.s;
import com.miui.securitycenter.R;
import com.miui.securityscan.cards.n;
import java.util.List;

class m extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f7662a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Context f7663b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ n f7664c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ List f7665d;
    final /* synthetic */ n e;

    m(n nVar, boolean z, Context context, n nVar2, List list) {
        this.e = nVar;
        this.f7662a = z;
        this.f7663b = context;
        this.f7664c = nVar2;
        this.f7665d = list;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        n nVar;
        int i;
        Object[] objArr;
        Context context;
        List<aa> b2 = C0514s.c().b();
        if (this.f7662a) {
            long j = C0501e.a(this.f7663b, b2).f6879a;
            nVar = this.f7664c;
            context = this.f7663b;
            i = R.string.menu_summary_power_manager_4;
            objArr = new Object[]{s.d(context, j)};
        } else {
            long a2 = C0520y.a(this.f7663b, b2);
            nVar = this.f7664c;
            context = this.f7663b;
            i = R.string.menu_summary_power_manager_2;
            objArr = new Object[]{s.d(context, a2)};
        }
        nVar.l = context.getString(i, objArr);
        return null;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(Void voidR) {
        for (n.c onPowerCenterChange : this.f7665d) {
            n nVar = this.f7664c;
            onPowerCenterChange.onPowerCenterChange(nVar.m, nVar.j, nVar.k, 2, nVar.l);
        }
    }
}
