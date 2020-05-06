package com.miui.monthreport;

import android.content.Context;
import android.location.Address;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.h.e;
import b.b.c.h.j;
import b.b.c.j.f;
import b.b.c.j.i;
import b.b.c.j.z;
import com.miui.luckymoney.config.Constants;
import com.miui.monthreport.d;
import com.miui.securitycenter.h;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.json.JSONObject;

public class l {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static final String f5649a = "l";

    /* renamed from: b  reason: collision with root package name */
    private static l f5650b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public static final Executor f5651c = Executors.newSingleThreadExecutor();
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public b f5652d;
    /* access modifiers changed from: private */
    public Context e;
    /* access modifiers changed from: private */
    public Exception f = null;
    /* access modifiers changed from: private */
    public List<String> g = null;
    /* access modifiers changed from: private */
    public volatile boolean h = false;
    /* access modifiers changed from: private */
    public Handler i = new i(this);

    private class a extends AsyncTask<Void, Void, Void> {

        /* renamed from: a  reason: collision with root package name */
        private Location f5653a;

        /* renamed from: b  reason: collision with root package name */
        private Context f5654b;

        public a(Context context, Location location) {
            this.f5654b = context;
            this.f5653a = location;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            Address a2 = i.a(this.f5654b, this.f5653a);
            if (a2 == null) {
                return null;
            }
            g.b(a2.getAdminArea());
            g.a(System.currentTimeMillis());
            return null;
        }
    }

    private class b extends AsyncTask<Void, Void, List<String>> {
        private b() {
        }

        /* synthetic */ b(l lVar, i iVar) {
            this();
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public List<String> doInBackground(Void... voidArr) {
            Calendar instance = Calendar.getInstance();
            instance.add(5, -30);
            int a2 = l.this.f5652d.a(instance.getTimeInMillis());
            String a3 = l.f5649a;
            Log.i(a3, "Old data cleaned : " + a2);
            return l.this.f5652d.b();
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(List<String> list) {
            super.onPostExecute(list);
            List unused = l.this.g = list;
            l.this.e();
        }
    }

    private class c extends AsyncTask<String, Void, Integer> {

        /* renamed from: a  reason: collision with root package name */
        private h f5657a;

        public c(h hVar) {
            this.f5657a = hVar;
        }

        private boolean a() {
            return h.i() && f.b(l.this.e) && g.b();
        }

        private boolean a(String str) {
            if (TextUtils.isEmpty(str)) {
                return false;
            }
            JSONObject jSONObject = new JSONObject(str);
            if (jSONObject.has("error_code")) {
                String a2 = l.f5649a;
                Log.i(a2, "Error : " + jSONObject.optString("error", "Unknown error"));
                return false;
            }
            l lVar = l.this;
            Exception unused = lVar.f = lVar.f5652d.a(this.f5657a.a());
            Log.i(l.f5649a, "Upload successfully");
            return true;
        }

        private boolean b(String str) {
            int a2;
            JSONObject jSONObject = new JSONObject();
            jSONObject.put(Constants.JSON_KEY_MODULE, str);
            String b2 = e.b(l.this.e, e.e, jSONObject, "5fdd8678-cddf-4269-bb73-48187445bba7", new j("monthreport_taskmanager"));
            String a3 = l.f5649a;
            Log.d(a3, "Available : " + b2);
            if (TextUtils.isEmpty(b2)) {
                return false;
            }
            JSONObject jSONObject2 = new JSONObject(b2);
            if (jSONObject2.has("error_code")) {
                return false;
            }
            if (!(jSONObject2.optInt("code", 0) == 1)) {
                return false;
            }
            if (!f.a(l.this.e) || ((a2 = l.this.f5652d.a(str)) > 0 && a2 < 20000)) {
                return true;
            }
            Log.d(l.f5649a, "Rejected : many events upload in mobile network");
            return false;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Integer doInBackground(String... strArr) {
            if (strArr.length < 0) {
                return 103;
            }
            try {
                String str = strArr[0];
                if (this.f5657a == null) {
                    Log.i(l.f5649a, String.format("Module %s task is null.", new Object[]{str}));
                    this.f5657a = h.a(str, l.this.f);
                }
                String d2 = this.f5657a.d();
                if (d2 == null) {
                    Log.i(l.f5649a, String.format("Module %s has no data.", new Object[]{str}));
                    return 103;
                }
                if (a()) {
                    if (!b(str)) {
                        return 103;
                    }
                    String a2 = l.f5649a;
                    Log.i(a2, "Uploading " + this.f5657a.toString());
                    if (a(e.a(l.this.e, e.f5640c, this.f5657a.e(), d2, "5fdd8678-cddf-4269-bb73-48187445bba7", new j("monthreport_taskmanager_uploadtask")))) {
                        return 101;
                    }
                }
                return 102;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Integer num) {
            super.onPostExecute(num);
            if (num != null) {
                switch (num.intValue()) {
                    case 101:
                        l.this.i.sendMessageDelayed(l.this.i.obtainMessage(101, this.f5657a.c()), 300000);
                        return;
                    case 102:
                        h hVar = this.f5657a;
                        if (hVar != null) {
                            hVar.f();
                            if (this.f5657a.b() < 3) {
                                l.this.i.sendMessageDelayed(l.this.i.obtainMessage(102, this.f5657a), ((long) this.f5657a.b()) * 300000);
                                return;
                            }
                            return;
                        }
                        return;
                    case 103:
                        l.this.i.sendEmptyMessage(103);
                        return;
                    default:
                        return;
                }
            }
        }
    }

    private l(Context context) {
        this.e = context.getApplicationContext();
        this.f5652d = b.a();
    }

    public static synchronized l a(Context context) {
        l lVar;
        synchronized (l.class) {
            if (f5650b == null) {
                f5650b = new l(context);
            }
            lVar = f5650b;
        }
        return lVar;
    }

    /* access modifiers changed from: private */
    public void a(String str, h hVar) {
        this.i.removeMessages(102);
        this.i.removeMessages(101);
        new c(hVar).executeOnExecutor(f5651c, new String[]{str});
    }

    /* access modifiers changed from: private */
    public void e() {
        List<String> list = this.g;
        if (list == null || list.isEmpty()) {
            Log.i(f5649a, "Module is null");
            this.h = false;
            return;
        }
        this.g.remove(0);
        a(this.g.get(0), (h) null);
    }

    public void c() {
        this.i.post(new k(this));
    }

    public void d() {
        long a2 = g.a();
        if (a2 == 0 || z.a(a2) >= 3) {
            d.a(this.e).a(false, new j(this), d.a.NETWORK_PROVIDER, d.a.PASSIVE_PROVIDER);
        }
        a(this.e).c();
    }
}
