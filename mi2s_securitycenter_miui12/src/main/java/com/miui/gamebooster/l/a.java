package com.miui.gamebooster.l;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import com.miui.gamebooster.m.C0382m;
import com.miui.gamebooster.model.C0395a;
import com.miui.securitycenter.h;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class a extends AsyncTask<Void, Void, C0395a> {

    /* renamed from: a  reason: collision with root package name */
    private Context f4442a;

    /* renamed from: b  reason: collision with root package name */
    private String f4443b;

    /* renamed from: c  reason: collision with root package name */
    private String f4444c;

    /* renamed from: d  reason: collision with root package name */
    private String f4445d;
    private C0047a e;

    /* renamed from: com.miui.gamebooster.l.a$a  reason: collision with other inner class name */
    public interface C0047a {
        void a(C0395a aVar);
    }

    public a(Activity activity, String str, String str2, C0047a aVar, String str3) {
        this.f4442a = activity.getApplicationContext();
        this.f4443b = str;
        this.f4444c = str2;
        this.e = aVar;
        this.f4445d = str3;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public C0395a doInBackground(Void... voidArr) {
        C0395a aVar = null;
        if (isCancelled()) {
            return null;
        }
        Process.setThreadPriority(19);
        try {
            if (!h.i()) {
                Process.setThreadPriority(0);
                return null;
            }
            String b2 = C0382m.b(this.f4443b, this.f4444c, this.f4442a);
            if (!TextUtils.isEmpty(b2)) {
                aVar = C0395a.a(new JSONObject(b2));
            }
            HashMap hashMap = new HashMap();
            hashMap.put("packageName", this.f4445d);
            String a2 = C0395a.a((Map<String, String>) hashMap);
            C0395a a3 = C0395a.a(new JSONObject(a2));
            if (a3 != null) {
                if (!a3.a().isEmpty()) {
                    C0382m.a(this.f4443b, this.f4444c, a2, this.f4442a);
                    aVar = a3;
                }
                Process.setThreadPriority(0);
                return aVar;
            }
            Process.setThreadPriority(0);
            return aVar;
        } catch (Exception e2) {
            Log.e("LoadAppInfoTask", "msg", e2);
        } catch (Throwable th) {
            Process.setThreadPriority(0);
            throw th;
        }
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(C0395a aVar) {
        C0047a aVar2;
        if (aVar != null && (aVar2 = this.e) != null) {
            aVar2.a(aVar);
        }
    }
}
