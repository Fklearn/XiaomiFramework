package com.miui.permcenter.settings.model;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.miui.permcenter.privacymanager.a.d;
import com.miui.permcenter.privacymanager.behaviorrecord.o;
import com.miui.securitycenter.Application;
import java.util.ArrayList;
import java.util.HashMap;

public class e extends AsyncTask<String, Void, HashMap<Long, ArrayList<d>>> {

    /* renamed from: a  reason: collision with root package name */
    private a f6546a;

    public interface a {
        void a(HashMap<Long, ArrayList<d>> hashMap);
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public HashMap<Long, ArrayList<d>> doInBackground(String... strArr) {
        try {
            return o.f((Context) Application.d());
        } catch (Exception e) {
            Log.e("GetPermissionDataTask", "get permission data error", e);
            return null;
        }
    }

    public void a(a aVar) {
        this.f6546a = aVar;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(HashMap<Long, ArrayList<d>> hashMap) {
        a aVar = this.f6546a;
        if (aVar != null) {
            aVar.a(hashMap);
        }
    }
}
