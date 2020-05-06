package com.miui.powercenter.utils;

import android.util.Log;
import com.miui.antivirus.service.VirusAutoUpdateJobService;
import java.util.ArrayList;
import java.util.Set;
import org.json.JSONObject;

class c implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Runnable f7296a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ d f7297b;

    c(d dVar, Runnable runnable) {
        this.f7297b = dVar;
        this.f7296a = runnable;
    }

    public void run() {
        Set<String> stringSet;
        Log.d("ChargeReporter", "start upload.");
        synchronized (this.f7297b.e) {
            stringSet = this.f7297b.d().getStringSet("charge_report_list", (Set) null);
        }
        if (stringSet != null && stringSet.size() > 0) {
            ArrayList arrayList = new ArrayList();
            for (String jSONObject : stringSet) {
                try {
                    arrayList.add(ChargeInfo.from(new JSONObject(jSONObject)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (arrayList.size() > 0) {
                this.f7297b.a((ArrayList<ChargeInfo>) arrayList);
            }
            this.f7297b.d().edit().putStringSet("charge_report_list", (Set) null).apply();
            Runnable runnable = this.f7296a;
            if (runnable != null) {
                ((VirusAutoUpdateJobService.b) runnable).a();
            }
        }
    }
}
