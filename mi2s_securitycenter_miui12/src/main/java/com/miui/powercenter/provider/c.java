package com.miui.powercenter.provider;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.miui.powercenter.batteryhistory.C0501e;
import com.miui.powercenter.batteryhistory.C0514s;
import com.miui.powercenter.batteryhistory.aa;
import java.util.List;

class c extends AsyncTask<Void, Void, C0501e.a> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f7172a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ d f7173b;

    c(d dVar, Context context) {
        this.f7173b = dVar;
        this.f7172a = context;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public C0501e.a doInBackground(Void... voidArr) {
        List<aa> b2 = C0514s.c().b();
        Log.i("BatteryInfoReceiver", "update charge detail " + b2.size());
        return C0501e.a(this.f7172a, b2);
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(C0501e.a aVar) {
        C0501e.a unused = this.f7173b.e = aVar;
    }
}
