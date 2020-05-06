package com.miui.antivirus.result;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import com.miui.activityutil.o;
import com.miui.common.customview.AdImageView;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.h;
import com.miui.securityscan.a.C0536b;
import com.miui.securityscan.cards.g;
import com.miui.securityscan.cards.k;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import miui.os.Build;
import org.json.JSONObject;

public class K {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static C0247j f2806a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public static WeakReference<Runnable> f2807b;

    /* renamed from: c  reason: collision with root package name */
    private static WeakReference<Activity> f2808c;

    /* renamed from: d  reason: collision with root package name */
    private Context f2809d;
    private ScanResultFrame e;
    private t f;
    /* access modifiers changed from: private */
    public ArrayList<C0238a> g;
    private a h;
    private k.a i;
    private g.a j;

    private static class a extends Handler {
        private a() {
        }

        /* synthetic */ a(I i) {
            this();
        }

        public void handleMessage(Message message) {
            K.a("VIEW", (C0243f) message.obj);
        }
    }

    public static class b {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public Context f2810a;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public ScanResultFrame f2811b;
        /* access modifiers changed from: private */

        /* renamed from: c  reason: collision with root package name */
        public t f2812c;
        /* access modifiers changed from: private */

        /* renamed from: d  reason: collision with root package name */
        public ArrayList<C0238a> f2813d = new ArrayList<>();

        public b(Context context) {
            this.f2810a = context;
        }

        public b a(ScanResultFrame scanResultFrame) {
            this.f2811b = scanResultFrame;
            return this;
        }

        public b a(t tVar) {
            this.f2812c = tVar;
            return this;
        }

        public b a(ArrayList<C0238a> arrayList) {
            this.f2813d = arrayList;
            return this;
        }

        public K a() {
            return new K(this, (I) null);
        }
    }

    public interface c {
        void a(N n);
    }

    private static class d extends AsyncTask<Void, Void, Void> {
        private d() {
        }

        /* synthetic */ d(I i) {
            this();
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            SharedPreferences.Editor putBoolean;
            Runnable runnable;
            Process.setThreadPriority(19);
            k.a((Context) Application.d());
            SharedPreferences sharedPreferences = Application.d().getSharedPreferences("data_config", 0);
            if (!h.i()) {
                C0247j unused = K.f2806a = C0250m.a();
            }
            if (K.f2806a == null) {
                String string = sharedPreferences.getString("layout_data", (String) null);
                if (!TextUtils.isEmpty(string)) {
                    try {
                        C0247j unused2 = K.f2806a = C0247j.a(new JSONObject(string), !Build.IS_INTERNATIONAL_BUILD);
                    } catch (Exception e) {
                        Log.e("CleanResultControl", "exception when load cache in PreloadDataTask :", e);
                    }
                }
            }
            try {
                if (K.f2806a == null || "******************".equals(K.f2806a.a())) {
                    C0247j unused3 = K.f2806a = C0250m.a();
                }
                WeakReference b2 = K.f2807b;
                if (!(b2 == null || (runnable = (Runnable) b2.get()) == null)) {
                    new Handler(Looper.getMainLooper()).post(runnable);
                }
                if (Build.IS_INTERNATIONAL_BUILD) {
                    C0251n.a("", "");
                }
                boolean z = sharedPreferences.getBoolean("initSucess", false);
                HashMap hashMap = new HashMap();
                if (!z) {
                    hashMap.put("init", o.f2310b);
                }
                String a2 = C0247j.a((Map<String, String>) hashMap);
                C0247j a3 = C0247j.a(new JSONObject(a2), true);
                if (a3 == null) {
                    C0247j unused4 = K.f2806a = C0250m.a();
                    putBoolean = sharedPreferences.edit().remove("layout_data");
                } else {
                    if (!a3.b().isEmpty()) {
                        C0247j unused5 = K.f2806a = a3;
                        sharedPreferences.edit().putString("layout_data", a2).apply();
                    }
                    if (a3.d()) {
                        putBoolean = sharedPreferences.edit().putBoolean("initSucess", true);
                    }
                    Process.setThreadPriority(0);
                    return null;
                }
                putBoolean.apply();
            } catch (Exception e2) {
                Log.e("CleanResultControl", "exception when ", e2);
            }
            Process.setThreadPriority(0);
            return null;
        }
    }

    private static class e extends AsyncTask<Void, Void, N> {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<c> f2814a;

        e(c cVar) {
            this.f2814a = new WeakReference<>(cVar);
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:12:0x0044 A[RETURN] */
        /* JADX WARNING: Removed duplicated region for block: B:13:0x0045 A[SYNTHETIC, Splitter:B:13:0x0045] */
        /* JADX WARNING: Removed duplicated region for block: B:8:0x0029  */
        /* JADX WARNING: Removed duplicated region for block: B:9:0x002c  */
        /* renamed from: a */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public com.miui.antivirus.result.N doInBackground(java.lang.Void... r7) {
            /*
                r6 = this;
                java.lang.String r7 = "data"
                java.util.HashMap r0 = new java.util.HashMap
                r0.<init>()
                boolean r1 = com.miui.securityscan.M.l()
                boolean r2 = com.miui.securityscan.M.m()
                java.lang.String r3 = "setting"
                if (r2 != 0) goto L_0x0019
                java.lang.String r1 = "2"
            L_0x0015:
                r0.put(r3, r1)
                goto L_0x001e
            L_0x0019:
                if (r1 == 0) goto L_0x001e
                java.lang.String r1 = "1"
                goto L_0x0015
            L_0x001e:
                java.lang.String r1 = "channel"
                java.lang.String r2 = "01-27"
                r0.put(r1, r2)
                boolean r1 = miui.os.Build.IS_INTERNATIONAL_BUILD
                if (r1 == 0) goto L_0x002c
                java.lang.String r1 = "https://adv.sec.intl.miui.com/info/layout"
                goto L_0x002e
            L_0x002c:
                java.lang.String r1 = "https://adv.sec.miui.com/info/layout"
            L_0x002e:
                com.miui.securityscan.i.k$a r2 = com.miui.securityscan.i.k.a.POST
                b.b.c.h.j r3 = new b.b.c.h.j
                java.lang.String r4 = "antivirus_scanresultcontrol"
                r3.<init>(r4)
                java.lang.String r4 = "5cdd8678-cddf-4269-ab73-48387445bba6"
                java.lang.String r0 = com.miui.securityscan.i.k.a((java.util.Map<java.lang.String, java.lang.String>) r0, (java.lang.String) r1, (com.miui.securityscan.i.k.a) r2, (java.lang.String) r4, (b.b.c.h.j) r3)
                boolean r1 = android.text.TextUtils.isEmpty(r0)
                r2 = 0
                if (r1 == 0) goto L_0x0045
                return r2
            L_0x0045:
                org.json.JSONObject r1 = new org.json.JSONObject     // Catch:{ Exception -> 0x0073 }
                r1.<init>(r0)     // Catch:{ Exception -> 0x0073 }
                org.json.JSONArray r0 = r1.getJSONArray(r7)     // Catch:{ Exception -> 0x0073 }
                r1 = 0
            L_0x004f:
                int r3 = r0.length()     // Catch:{ Exception -> 0x0073 }
                if (r1 >= r3) goto L_0x0073
                org.json.JSONObject r3 = r0.getJSONObject(r1)     // Catch:{ Exception -> 0x0073 }
                java.lang.String r4 = "type"
                java.lang.String r4 = r3.optString(r4)     // Catch:{ Exception -> 0x0073 }
                java.lang.String r5 = "002"
                boolean r4 = r5.equals(r4)     // Catch:{ Exception -> 0x0073 }
                if (r4 == 0) goto L_0x0070
                org.json.JSONObject r7 = r3.optJSONObject(r7)     // Catch:{ Exception -> 0x0073 }
                com.miui.antivirus.result.N r2 = com.miui.antivirus.result.N.b(r7)     // Catch:{ Exception -> 0x0073 }
                goto L_0x0073
            L_0x0070:
                int r1 = r1 + 1
                goto L_0x004f
            L_0x0073:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.antivirus.result.K.e.doInBackground(java.lang.Void[]):com.miui.antivirus.result.N");
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(N n) {
            c cVar;
            if (n != null && (cVar = (c) this.f2814a.get()) != null) {
                cVar.a(n);
            }
        }
    }

    private K(b bVar) {
        this.g = new ArrayList<>();
        this.h = new a((I) null);
        this.i = new I(this);
        this.j = new J(this);
        this.f2809d = bVar.f2810a;
        this.f = bVar.f2812c;
        this.g = bVar.f2813d;
        this.e = bVar.f2811b;
        this.e.a(this.f2809d, f2806a, this.f);
        this.e.a();
        k.a(this.f2809d).a(this.i);
        g.a(this.f2809d).b(this.j);
    }

    /* synthetic */ K(b bVar, I i2) {
        this(bVar);
    }

    public static void a(Activity activity) {
        WeakReference<Activity> weakReference = f2808c;
        if (weakReference == null) {
            f2806a = null;
            return;
        }
        Activity activity2 = (Activity) weakReference.get();
        if (activity2 == null || activity == activity2) {
            f2806a = null;
            f2808c = null;
        }
    }

    public static void a(c cVar, Context context) {
        if (b.b.b.c.g.a(context.getApplicationContext()).b()) {
            Log.d("CleanResultControl", "start load sidekick data");
            new e(cVar).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }

    public static void a(Runnable runnable) {
        if (runnable == null) {
            f2807b = null;
        } else {
            f2807b = new WeakReference<>(runnable);
        }
    }

    public static void a(String str, C0243f fVar) {
        if (!fVar.i()) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(new C0536b.d(str, fVar));
            C0536b.a((Context) Application.d(), (List<Object>) arrayList);
        }
    }

    public static void b(Activity activity) {
        f2808c = new WeakReference<>(activity);
        new d((I) null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    public static boolean d() {
        return f2806a != null;
    }

    public void a(b.b.c.i.b bVar) {
        this.e.setEventHandler(bVar);
    }

    public void a(C0238a aVar) {
        this.g.remove(aVar);
        f();
    }

    public void a(C0244g gVar, List<C0244g> list, List<C0244g> list2) {
        int indexOf = this.g.indexOf(gVar);
        if (indexOf > 0 && indexOf < this.g.size() - 1) {
            this.g.removeAll(list);
            for (C0244g add : list2) {
                indexOf++;
                this.g.add(indexOf, add);
            }
        }
        f();
    }

    public void a(AdImageView adImageView, int i2, C0243f fVar) {
        adImageView.a(this.h, i2, fVar);
    }

    public List<C0244g> c() {
        return this.e.getModels();
    }

    public void e() {
        this.e.b();
        k.a(this.f2809d).b(this.i);
        g.a(this.f2809d).d(this.j);
    }

    public void f() {
        t tVar = this.f;
        if (tVar != null) {
            tVar.notifyDataSetChanged();
        }
    }
}
