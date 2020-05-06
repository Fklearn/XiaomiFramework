package b.b.n;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.text.TextUtils;
import b.b.c.j.d;
import b.b.p.f;
import com.miui.applicationlock.c.o;
import com.miui.systemAdSolution.common.AdInfo;
import com.miui.systemAdSolution.common.AdTrackType;
import com.miui.systemAdSolution.common.Material;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class e implements a {

    /* renamed from: a  reason: collision with root package name */
    private static e f1853a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public f f1854b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public final Map<String, AdInfo> f1855c = new HashMap();

    /* renamed from: d  reason: collision with root package name */
    private final SharedPreferences f1856d;
    private long e;
    private final ArrayList<String> f = new ArrayList<>();
    private final Context g;

    private e(Context context) {
        this.g = context.getApplicationContext();
        this.f1856d = context.getSharedPreferences("skin_close_preference", 0);
    }

    public static synchronized e a(Context context) {
        e eVar;
        synchronized (e.class) {
            if (f1853a == null) {
                f1853a = new e(context);
            }
            eVar = f1853a;
        }
        return eVar;
    }

    /* access modifiers changed from: private */
    public void a() {
        this.f1854b = f.a(this.g);
        d dVar = new d(this);
        this.f1854b.a(l.f1872a, (f.c) dVar);
        this.f1854b.a(l.f1873b, (f.c) dVar);
    }

    private void b(String str) {
        d.a(new c(this, str));
    }

    private void c(String str) {
        SharedPreferences.Editor edit = this.f1856d.edit();
        Map<String, ?> all = this.f1856d.getAll();
        if (all != null && all.size() > 0) {
            for (Map.Entry next : all.entrySet()) {
                String str2 = (String) next.getKey();
                if (!TextUtils.isEmpty(str2) && str2.startsWith(str)) {
                    edit.remove((String) next.getKey());
                }
            }
            edit.commit();
        }
    }

    public g a(String str) {
        Material a2;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        String str2 = l.f1874c.get(str);
        long j = this.e + 86400000;
        if (SystemClock.elapsedRealtime() > j) {
            this.f.clear();
        }
        if (SystemClock.elapsedRealtime() > j || SystemClock.elapsedRealtime() < 86400000 || !this.f.contains(str)) {
            b(str2);
            this.e = SystemClock.elapsedRealtime();
            if (!this.f.contains(str)) {
                this.f.add(str);
            }
        }
        AdInfo a3 = f.a(this.g, str2);
        this.f1855c.put(str, a3);
        if (!(a3 == null || a3.getId() == o.a(str))) {
            o.g(true);
        }
        if (a3 == null || a3.getId() == 0) {
            c(str);
            return null;
        } else if (!o.u() || (a2 = l.a(a3)) == null || a2.getResources() == null || a2.getResources().size() <= 0) {
            return null;
        } else {
            if (l.a(l.a(this.g, str) + File.separator + a3.getId()) != a2.getResources().size()) {
                return null;
            }
            o.a(str, a3.getId());
            return new g(this.g, a3, str);
        }
    }

    public void a(String str, String str2, AdTrackType.Type type, long j) {
        AdTrackType adTrackType = new AdTrackType(type);
        if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
            d.a(new b(this, str, str2, adTrackType, j));
        }
    }

    public boolean a(String str, long j) {
        SharedPreferences sharedPreferences = this.f1856d;
        return sharedPreferences.getBoolean(str + "_" + String.valueOf(j), false);
    }

    public boolean b(String str, long j) {
        SharedPreferences sharedPreferences = this.f1856d;
        return sharedPreferences.getBoolean(str + "_" + "redPoint" + "_" + String.valueOf(j), true);
    }

    public void c(String str, long j) {
        SharedPreferences.Editor edit = this.f1856d.edit();
        edit.putBoolean(str + "_" + "redPoint" + "_" + String.valueOf(j), false);
        edit.commit();
    }
}
