package com.miui.securityscan.g;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.miui.appmanager.c.m;
import com.miui.luckymoney.config.Constants;
import com.miui.securityscan.L;
import com.miui.securityscan.c.e;
import com.miui.securityscan.i.h;
import java.util.HashMap;

public class b extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    private Context f7708a;

    public b(L l) {
        Activity activity = l.getActivity();
        if (activity != null) {
            this.f7708a = activity.getApplicationContext();
        }
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        Context context;
        if (!isCancelled() && (context = this.f7708a) != null) {
            e a2 = e.a(context, "data_config");
            try {
                Thread.sleep(300);
            } catch (Exception unused) {
            }
            HashMap hashMap = new HashMap();
            hashMap.put(Constants.JSON_KEY_DATA_VERSION, a2.a("dataVsersionAm", ""));
            try {
                h.a(this.f7708a, "app_manager_adv", m.a(this.f7708a, hashMap));
            } catch (Exception e) {
                Log.e("LoadAppManagerAdvTask", "loadAppManagerAdv writeStringToFileDir error", e);
            }
        }
        return null;
    }
}
