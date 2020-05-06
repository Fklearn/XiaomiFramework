package com.miui.optimizecenter.storage;

import android.util.Log;
import b.b.i.a.b;
import com.miui.securitycenter.Application;
import com.miui.securityscan.i.k;
import java.util.HashSet;
import java.util.List;
import org.json.JSONArray;

public class n {

    /* renamed from: a  reason: collision with root package name */
    private static n f5763a;

    /* renamed from: b  reason: collision with root package name */
    private a f5764b;

    public interface a {
        void a(String str);
    }

    private n() {
    }

    public static n a() {
        if (f5763a == null) {
            f5763a = new n();
        }
        return f5763a;
    }

    public void a(List<String> list) {
        HashSet<String> hashSet = new HashSet<>(list);
        JSONArray jSONArray = new JSONArray();
        for (String put : hashSet) {
            jSONArray.put(put);
        }
        if (com.miui.securityscan.c.a.f7625a) {
            Log.i("RemoteDirDataManager", "loadRemoteDirData: request pkg = " + jSONArray.toString());
        }
        String a2 = k.a(jSONArray.toString(), b.f1782a);
        if (com.miui.securityscan.c.a.f7625a) {
            Log.i("RemoteDirDataManager", "loadRemoteDirData: response = " + a2);
        }
        String a3 = b.b.i.a.a.a(a2, "0123456789123456");
        if (com.miui.securityscan.c.a.f7625a) {
            Log.i("RemoteDirDataManager", "loadRemoteDirData: result = " + a3);
        }
        a aVar = this.f5764b;
        if (aVar != null) {
            aVar.a(a3);
        }
        Application.d().getSharedPreferences("storagePathDB", 0).edit().putLong("lastUpdateTime", System.currentTimeMillis()).apply();
    }
}
