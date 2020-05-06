package com.miui.permcenter.settings.model;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.miui.permcenter.privacymanager.behaviorrecord.o;
import com.miui.securitycenter.Application;
import java.util.HashMap;

public class d extends AsyncTask<String, Void, HashMap<Long, Integer>> {

    /* renamed from: a  reason: collision with root package name */
    private a f6545a;

    public interface a {
        void a(HashMap<Long, Integer> hashMap);
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public HashMap<Long, Integer> doInBackground(String... strArr) {
        try {
            return o.e((Context) Application.d());
        } catch (Exception e) {
            Log.e("GetAllAllowAppTask", "get all app data error", e);
            return null;
        }
    }

    public void a(a aVar) {
        this.f6545a = aVar;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(HashMap<Long, Integer> hashMap) {
        a aVar = this.f6545a;
        if (aVar != null) {
            aVar.a(hashMap);
        }
    }
}
