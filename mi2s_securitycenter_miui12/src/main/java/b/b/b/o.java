package b.b.b;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import b.b.b.d.d;
import b.b.b.d.k;
import b.b.b.d.m;
import b.b.c.j.B;
import b.b.c.j.i;
import b.b.c.j.x;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.miui.antivirus.model.a;
import com.miui.antivirus.model.e;
import com.miui.antivirus.model.j;
import com.miui.antivirus.model.l;
import com.miui.antivirus.service.GuardService;
import com.miui.guardprovider.VirusObserver;
import com.miui.guardprovider.WifiDetectObserver;
import com.miui.guardprovider.aidl.IAntiVirusServer;
import com.miui.guardprovider.aidl.IVirusObserver;
import com.miui.networkassistant.config.Constants;
import com.miui.permcenter.n;
import com.miui.permission.PermissionManager;
import com.miui.securityscan.model.system.VirusScanModel;
import java.io.Closeable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import miui.os.Build;
import miui.util.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class o {

    /* renamed from: a  reason: collision with root package name */
    private static o f1560a;
    /* access modifiers changed from: private */
    public AtomicBoolean A = new AtomicBoolean(false);
    /* access modifiers changed from: private */
    public AtomicInteger B = new AtomicInteger(0);
    private e C = new e(this, (j) null);
    private final IPackageDeleteObserver.Stub D = new l(this);
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public List<String> f1561b = new ArrayList();

    /* renamed from: c  reason: collision with root package name */
    private b f1562c;

    /* renamed from: d  reason: collision with root package name */
    private VirusObserver f1563d = new j(this);
    private VirusObserver e = new k(this);
    /* access modifiers changed from: private */
    public Context f;
    /* access modifiers changed from: private */
    public Long g = 0L;
    /* access modifiers changed from: private */
    public PackageManager h;
    private com.miui.antivirus.model.e i;
    private com.miui.antivirus.model.e j;
    private l k = new l();
    private Map<String, com.miui.antivirus.model.e> l = new HashMap();
    private Map<String, com.miui.antivirus.model.e> m = new HashMap();
    private boolean n = false;
    /* access modifiers changed from: private */
    public final Object o = new Object();
    private final Object p = new Object();
    private long q = 0;
    private long r = 0;
    private long s = 0;
    /* access modifiers changed from: private */
    public long t = 0;
    /* access modifiers changed from: private */
    public long u = 0;
    private JSONArray v = new JSONArray();
    private JSONArray w = new JSONArray();
    private Map<String, com.miui.antivirus.model.e> x = new HashMap();
    private ArrayList<String> y = new ArrayList<>();
    private int z;

    class a extends AsyncTask<Void, Void, List<b.b.b.d.b>> {

        /* renamed from: a  reason: collision with root package name */
        private c f1564a;

        /* renamed from: b  reason: collision with root package name */
        private JSONArray f1565b;

        public a(c cVar, JSONArray jSONArray) {
            this.f1564a = cVar;
            this.f1565b = jSONArray;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public List<b.b.b.d.b> doInBackground(Void... voidArr) {
            b.b.b.d.b bVar;
            ArrayList arrayList = new ArrayList();
            try {
                b.b.b.d.d dVar = new b.b.b.d.d(b.b.b.d.e.e, o.this.f);
                dVar.getClass();
                new d.C0025d(dVar).a("params", this.f1565b.toString());
                Log.i("PaySafetyCheckManager", "active request url = " + b.b.b.d.e.e);
                if (dVar.b() == d.c.OK) {
                    JSONArray a2 = dVar.a();
                    if (b.b.b.d.e.f1524a) {
                        Log.i("PaySafetyCheckManager", "obj = " + a2);
                    }
                    List<b.b.b.d.b> a3 = b.b.b.d.f.a(a2);
                    for (int i = 0; i < a3.size(); i++) {
                        if (a3.get(i) == null) {
                            bVar = new b.b.b.d.b(5);
                        } else {
                            bVar = a3.get(i);
                            ApplicationInfo applicationInfo = o.this.h.getApplicationInfo(bVar.k, 0);
                            bVar.h = applicationInfo.loadLabel(o.this.h).toString();
                            bVar.l = applicationInfo.sourceDir.toString();
                        }
                        arrayList.add(bVar);
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("PaySafetyCheckManager", "NameNotFoundException when check sign background: ", e);
            }
            return arrayList;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(List<b.b.b.d.b> list) {
            Iterator<b.b.b.d.b> it = list.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                b.b.b.d.b next = it.next();
                if (next.f1504a == 2) {
                    this.f1564a.a(3);
                    Log.i("PaySafetyCheckManager", "background scan : unofficial app risk = " + next.k);
                    break;
                }
            }
            this.f1564a.e();
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
        }
    }

    class b extends AsyncTask<Void, Void, List<b.b.b.d.b>> {

        /* renamed from: a  reason: collision with root package name */
        private d f1567a;

        /* renamed from: b  reason: collision with root package name */
        private JSONArray f1568b;

        /* renamed from: c  reason: collision with root package name */
        private long f1569c;

        /* renamed from: d  reason: collision with root package name */
        private int f1570d;

        public b(d dVar, JSONArray jSONArray, int i) {
            this.f1567a = dVar;
            this.f1568b = jSONArray;
            this.f1570d = i;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public List<b.b.b.d.b> doInBackground(Void... voidArr) {
            b.b.b.d.b bVar;
            ArrayList arrayList = new ArrayList();
            try {
                b.b.b.d.d dVar = new b.b.b.d.d(b.b.b.d.e.e, o.this.f);
                dVar.getClass();
                new d.C0025d(dVar).a("params", this.f1568b.toString());
                Log.i("PaySafetyCheckManager", "request url = " + b.b.b.d.e.e);
                d.c b2 = dVar.b();
                if (this.f1570d != o.this.B.get()) {
                    return arrayList;
                }
                if (b2 == d.c.OK) {
                    JSONArray a2 = dVar.a();
                    if (b.b.b.d.e.f1524a) {
                        Log.i("PaySafetyCheckManager", "obj = " + a2);
                    }
                    List<b.b.b.d.b> a3 = b.b.b.d.f.a(a2);
                    for (int i = 0; i < a3.size(); i++) {
                        if (a3.get(i) == null) {
                            bVar = new b.b.b.d.b(5);
                        } else {
                            bVar = a3.get(i);
                            ApplicationInfo applicationInfo = o.this.h.getApplicationInfo(bVar.k, 0);
                            bVar.h = applicationInfo.loadLabel(o.this.h).toString();
                            bVar.l = applicationInfo.sourceDir.toString();
                        }
                        arrayList.add(bVar);
                    }
                } else {
                    for (int i2 = 0; i2 < this.f1568b.length(); i2++) {
                        ApplicationInfo applicationInfo2 = o.this.h.getApplicationInfo(((JSONObject) this.f1568b.get(i2)).optString("packageName"), 0);
                        b.b.b.d.b bVar2 = new b.b.b.d.b(3);
                        bVar2.h = applicationInfo2.loadLabel(o.this.h).toString();
                        arrayList.add(bVar2);
                    }
                }
                return arrayList;
            } catch (PackageManager.NameNotFoundException | JSONException e2) {
                Log.e("PaySafetyCheckManager", "exception when check sign foreground: ", e2);
            }
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(List<b.b.b.d.b> list) {
            if (this.f1570d == o.this.B.get()) {
                for (b.b.b.d.b next : list) {
                    com.miui.antivirus.model.e eVar = new com.miui.antivirus.model.e();
                    eVar.a(f.INSTALLED_APP);
                    eVar.f(next.k);
                    eVar.c(next.f1505b);
                    eVar.b(next.h);
                    eVar.g(next.l);
                    eVar.a(e.b.APP);
                    eVar.a(e.a.SIGN);
                    eVar.b(next.f1504a);
                    this.f1567a.a(eVar);
                }
                long unused = o.this.u = System.currentTimeMillis() - this.f1569c;
                if (o.this.A.getAndSet(!o.this.A.get())) {
                    Log.w("PaySafetyCheckManager", "virus scan first finished, now signature scan finished !");
                    this.f1567a.c();
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            this.f1569c = System.currentTimeMillis();
        }
    }

    public interface c {
        void a(int i);

        void b();

        void c();

        void d();

        void e();
    }

    public interface d {
        void a(int i);

        void a(a.C0039a aVar);

        void a(com.miui.antivirus.model.e eVar);

        void a(l.a aVar, boolean z);

        void b();

        void c();

        void d();

        void e();

        void f();

        void g();

        boolean isCancelled();
    }

    private class e extends Handler {
        private e() {
        }

        /* synthetic */ e(o oVar, j jVar) {
            this();
        }

        public void handleMessage(Message message) {
            if (message.what == 1) {
                ((c) message.obj).d();
            }
        }
    }

    public enum f {
        INSTALLED_APP,
        UNINSTALLED_APK
    }

    public enum g {
        SAFE,
        RISK,
        VIRUS
    }

    private o(Context context) {
        this.f = context.getApplicationContext();
        this.h = this.f.getPackageManager();
    }

    private void A() {
        try {
            Iterator<PackageInfo> it = x.c(this.f).iterator();
            while (it.hasNext()) {
                PackageInfo next = it.next();
                ApplicationInfo applicationInfo = next.applicationInfo;
                com.miui.antivirus.model.e eVar = new com.miui.antivirus.model.e();
                eVar.b(applicationInfo.loadLabel(this.h).toString());
                eVar.f(applicationInfo.packageName);
                eVar.c(next.versionName);
                eVar.g(applicationInfo.sourceDir);
                eVar.a(f.INSTALLED_APP);
                this.x.put(applicationInfo.sourceDir, eVar);
                if (!p.g().contains(next.packageName)) {
                    long currentTimeMillis = System.currentTimeMillis();
                    long nextLong = new Random().nextLong();
                    JSONObject jSONObject = new JSONObject();
                    try {
                        jSONObject.put("packageName", next.packageName);
                        jSONObject.put("versionCode", next.versionCode);
                        jSONObject.put("signatureHash", b.b.b.d.l.b(this.f, applicationInfo.packageName));
                        jSONObject.put("installerPackage", next.packageName);
                        jSONObject.put("timeStamp", currentTimeMillis);
                        jSONObject.put("nonce", nextLong);
                    } catch (JSONException e2) {
                        Log.e("PaySafetyCheckManager", "", e2);
                    }
                    this.v.put(jSONObject);
                }
            }
        } catch (Exception e3) {
            Log.e("PaySafetyCheckManager", "Exception in get foreground installed scanning packages: ", e3);
        }
    }

    private void B() {
        this.y.clear();
        this.w = new JSONArray();
        try {
            for (String packageInfo : x.b(this.f)) {
                try {
                    PackageInfo packageInfo2 = this.h.getPackageInfo(packageInfo, 64);
                    ApplicationInfo applicationInfo = packageInfo2.applicationInfo;
                    if (x.a(applicationInfo)) {
                        this.y.add(applicationInfo.sourceDir);
                        if (!p.g().contains(packageInfo2.packageName)) {
                            long currentTimeMillis = System.currentTimeMillis();
                            long nextLong = new Random().nextLong();
                            JSONObject jSONObject = new JSONObject();
                            try {
                                jSONObject.put("packageName", packageInfo2.packageName);
                                jSONObject.put("versionCode", packageInfo2.versionCode);
                                jSONObject.put("signatureHash", b.b.b.d.l.b(this.f, packageInfo2.packageName));
                                jSONObject.put("installerPackage", packageInfo2.packageName);
                                jSONObject.put("timeStamp", currentTimeMillis);
                                jSONObject.put("nonce", nextLong);
                            } catch (JSONException e2) {
                                Log.e("PaySafetyCheckManager", "", e2);
                            }
                            this.w.put(jSONObject);
                        }
                    }
                } catch (PackageManager.NameNotFoundException unused) {
                }
            }
        } catch (Exception e3) {
            Log.e("PaySafetyCheckManager", "Exception in get background scanning packages :", e3);
        }
    }

    private void C() {
        Cursor cursor = null;
        try {
            cursor = this.f.getContentResolver().query(MediaStore.Files.getContentUri("external"), new String[]{"_data", "date_modified"}, "_data LIKE '%.apk'", (String[]) null, (String) null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String string = cursor.getString(0);
                    PackageInfo d2 = x.d(this.f, string);
                    if (d2 != null) {
                        com.miui.antivirus.model.e eVar = new com.miui.antivirus.model.e();
                        eVar.b(d2.applicationInfo.loadLabel(this.h).toString());
                        eVar.f(d2.packageName);
                        eVar.c(d2.versionName);
                        eVar.g(string);
                        eVar.a(f.UNINSTALLED_APK);
                        this.x.put(string, eVar);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e2) {
            Log.e("PaySafetyCheckManager", "Exception in get foreground scanning apks: ", e2);
        } catch (Throwable th) {
            IOUtils.closeQuietly((Closeable) null);
            throw th;
        }
        IOUtils.closeQuietly(cursor);
    }

    private boolean D() {
        try {
            String[] strArr = {"/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/"};
            for (int i2 = 0; i2 < strArr.length; i2++) {
                if (new File(strArr[i2] + "su").exists()) {
                    return true;
                }
            }
        } catch (Exception e2) {
            Log.e("PaySafetyCheckManager", "checkSystemRoot : ", e2);
        }
        return false;
    }

    private void E() {
        this.x.clear();
    }

    private void F() {
        this.v = new JSONArray();
    }

    private void G() {
        F();
        E();
        f();
        a();
        c();
        e();
        d();
        b();
        this.A.set(false);
        A();
        C();
        H();
        this.B.getAndIncrement();
    }

    private void H() {
        int i2 = p.p() ? k.b(this.f) ? 5 : 1 : 0;
        int i3 = !p.k() ? 2 : 3;
        if (!p.m()) {
            i3--;
        }
        int a2 = a((Boolean) false);
        if (Build.IS_INTERNATIONAL_BUILD) {
            i2 = 0;
        }
        this.z = a2 + i2 + i3 + 3;
    }

    private Intent a(IntentFilter intentFilter) {
        Uri uri;
        Intent intent = new Intent(intentFilter.getAction(0));
        if (intentFilter.countCategories() > 0 && !TextUtils.isEmpty(intentFilter.getCategory(0))) {
            intent.addCategory(intentFilter.getCategory(0));
        }
        String str = null;
        if (intentFilter.countDataSchemes() <= 0 || TextUtils.isEmpty(intentFilter.getDataScheme(0))) {
            uri = null;
        } else {
            uri = Uri.parse(intentFilter.getDataScheme(0) + ":");
        }
        if (intentFilter.countDataTypes() > 0 && !TextUtils.isEmpty(intentFilter.getDataType(0))) {
            str = intentFilter.getDataType(0);
            if (!str.contains("\\") && !str.contains("/")) {
                str = str + "/*";
            }
        }
        intent.setDataAndType(uri, str);
        return intent;
    }

    public static synchronized o a(Context context) {
        o oVar;
        synchronized (o.class) {
            if (f1560a == null) {
                f1560a = new o(context);
            }
            oVar = f1560a;
        }
        return oVar;
    }

    private void a(IAntiVirusServer iAntiVirusServer, String[] strArr, VirusObserver virusObserver) {
        try {
            iAntiVirusServer.a(strArr, (IVirusObserver) virusObserver, p.n());
        } catch (RemoteException e2) {
            Log.e("PaySafetyCheckManager", "startVirusScanTask Background: ", e2);
        }
    }

    private void a(IAntiVirusServer iAntiVirusServer, String[] strArr, VirusObserver virusObserver, d dVar, long j2) {
        try {
            synchronized (this.o) {
                if (this.g.longValue() == j2 && j2 > 0) {
                    int a2 = iAntiVirusServer.a(strArr, (IVirusObserver) virusObserver, p.n());
                    Log.i("PaySafetyCheckManager", "virusCheck  taskId =" + a2);
                    dVar.a(a2);
                }
            }
        } catch (RemoteException e2) {
            Log.e("PaySafetyCheckManager", "startVirusScanTask Foreground: ", e2);
        }
    }

    /* access modifiers changed from: private */
    public com.miui.antivirus.model.e b(String str) {
        return this.x.get(str);
    }

    private void b(c cVar) {
        if (p.k() && D()) {
            cVar.a(2);
            Log.i("PaySafetyCheckManager", "background scan : root risk !");
        }
    }

    private void b(d dVar) {
        boolean z2;
        com.miui.antivirus.model.e eVar = new com.miui.antivirus.model.e();
        eVar.a(e.b.APP);
        eVar.a(e.a.AUTH);
        Iterator<com.miui.permcenter.a> it = n.b(this.f, (long) PermissionManager.PERM_ID_READ_NOTIFICATION_SMS).iterator();
        while (true) {
            if (it.hasNext()) {
                if (it.next().f().get(Long.valueOf(PermissionManager.PERM_ID_READ_NOTIFICATION_SMS)).intValue() == 3) {
                    z2 = true;
                    break;
                }
            } else {
                z2 = false;
                break;
            }
        }
        eVar.f(z2);
        dVar.a(eVar);
    }

    private void c(c cVar) {
        if (!p.p()) {
            cVar.e();
            return;
        }
        if (!k.a(this.f)) {
            cVar.c();
        } else if (k.b(this.f)) {
            String h2 = p.h();
            if (!TextUtils.isEmpty(h2)) {
                try {
                    JSONObject jSONObject = new JSONObject(h2);
                    if (jSONObject.optBoolean("wifi_type_approve", false)) {
                        if (jSONObject.getBoolean("wifi_item_encryption") && !jSONObject.getBoolean("wifi_item_fake") && !jSONObject.getBoolean("wifi_item_dns") && !jSONObject.getBoolean("wifi_item_arp")) {
                            cVar.a(1);
                        }
                    }
                    if (jSONObject.getBoolean("wifi_item_encryption") || jSONObject.getBoolean("wifi_item_fake") || jSONObject.getBoolean("wifi_item_dns") || jSONObject.getBoolean("wifi_item_arp")) {
                        cVar.a(6);
                        Log.i("PaySafetyCheckManager", "background scan : wifi risk !");
                    }
                } catch (JSONException e2) {
                    Log.e("PaySafetyCheckManager", "", e2);
                }
            } else {
                Intent intent = new Intent(this.f, GuardService.class);
                intent.setAction("action_start_wifi_scan_task");
                intent.putExtra("build_wifi_cache_immediately", true);
                this.f.startService(intent);
            }
        }
        cVar.e();
    }

    private void c(d dVar) {
        if (!p.k() || !D()) {
            if (p.m() && i.j(this.f)) {
                j jVar = new j(e.b.APP);
                jVar.g(true);
                jVar.h(false);
                dVar.a((com.miui.antivirus.model.e) jVar);
            }
            dVar.g();
            return;
        }
        j jVar2 = new j(e.b.APP);
        jVar2.h(true);
        jVar2.g(false);
        dVar.a((com.miui.antivirus.model.e) jVar2);
        dVar.g();
    }

    private void d(d dVar) {
        if (p.p()) {
            String h2 = p.h();
            if (!k.b(this.f)) {
                dVar.a(l.a.CONNECTION, false);
            } else if (TextUtils.isEmpty(h2)) {
                dVar.a(l.a.CONNECTION, true);
                dVar.a(l.a.ENCRYPTION, false);
                dVar.a(l.a.DNS, false);
                dVar.a(l.a.FAKE, false);
                dVar.a(l.a.ARP, false);
                Intent intent = new Intent(this.f, GuardService.class);
                intent.setAction("action_start_wifi_scan_task");
                intent.putExtra("build_wifi_cache_immediately", true);
                this.f.startService(intent);
            } else {
                try {
                    JSONObject jSONObject = new JSONObject(h2);
                    dVar.a(l.a.CONNECTION, true);
                    dVar.a(l.a.ENCRYPTION, jSONObject.getBoolean("wifi_item_encryption"));
                    dVar.a(l.a.DNS, jSONObject.getBoolean("wifi_item_dns"));
                    dVar.a(l.a.FAKE, jSONObject.getBoolean("wifi_item_fake"));
                    dVar.a(l.a.ARP, jSONObject.getBoolean("wifi_item_arp"));
                } catch (JSONException e2) {
                    Log.e("PaySafetyCheckManager", "", e2);
                }
            }
        }
    }

    public int a(Boolean bool) {
        return bool.booleanValue() ? this.x.size() : Build.IS_INTERNATIONAL_BUILD ? this.x.size() : this.x.size() + this.v.length();
    }

    public g a(int i2) {
        return i2 != 2 ? i2 != 3 ? g.SAFE : g.RISK : g.VIRUS;
    }

    public void a() {
        this.i = null;
    }

    public void a(c cVar) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SENDTO");
        intentFilter.addDataScheme("smsto");
        PackageManager packageManager = this.h;
        Class cls = Integer.TYPE;
        ResolveInfo resolveInfo = (ResolveInfo) b.b.o.g.d.a("PaySafetyCheckManager", (Object) packageManager, "resolveActivityAsUser", (Class<?>[]) new Class[]{Intent.class, cls, cls}, a(intentFilter), 65536, Integer.valueOf(B.c()));
        if (resolveInfo != null) {
            String str = resolveInfo.activityInfo.packageName;
            if ((!b.b.b.d.n.d() || !"com.google.android.apps.messaging".equals(str)) && !"com.android.mms".equals(str) && !Constants.System.ANDROID_PACKAGE_NAME.equals(str) && !"com.jeejen.family.miui".equals(str)) {
                cVar.a(5);
                Log.i("PaySafetyCheckManager", "background scan : default messaging app risk = " + str);
            }
        }
    }

    public void a(d dVar) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SENDTO");
        intentFilter.addDataScheme("smsto");
        ResolveInfo resolveActivity = this.h.resolveActivity(a(intentFilter), 65536);
        if (resolveActivity != null) {
            String str = resolveActivity.activityInfo.packageName;
            if (!b.b.b.d.n.d() || !"com.google.android.apps.messaging".equals(str)) {
                if (!"com.android.mms".equals(str) && !Constants.System.ANDROID_PACKAGE_NAME.equals(str) && !"com.jeejen.family.miui".equals(str)) {
                    com.miui.antivirus.model.e eVar = new com.miui.antivirus.model.e();
                    eVar.a(f.INSTALLED_APP);
                    eVar.f(str);
                    eVar.b(resolveActivity.activityInfo.loadLabel(this.h).toString());
                    eVar.g(resolveActivity.activityInfo.applicationInfo.sourceDir);
                    eVar.a(e.b.APP);
                    eVar.a(e.a.SMS);
                    dVar.a(eVar);
                }
                dVar.e();
                return;
            }
            dVar.e();
        }
    }

    public void a(com.miui.antivirus.model.e eVar) {
        this.m.put(eVar.q(), eVar);
    }

    public void a(l.a aVar, boolean z2) {
        int i2 = n.f1559a[aVar.ordinal()];
        if (i2 == 1) {
            this.k.h(z2);
        } else if (i2 == 2) {
            this.k.j(!z2);
        } else if (i2 == 3) {
            this.k.i(z2);
        } else if (i2 == 4) {
            this.k.k(z2);
        } else if (i2 == 5) {
            this.k.g(z2);
        }
    }

    public void a(IAntiVirusServer iAntiVirusServer) {
        if (!Build.IS_INTERNATIONAL_BUILD) {
            synchronized (this.p) {
                b.b.b.d.o oVar = new b.b.b.d.o();
                WifiDetectObserver wifiDetectObserver = new WifiDetectObserver(this.f);
                wifiDetectObserver.a(oVar);
                oVar.a(iAntiVirusServer, wifiDetectObserver, true);
                Log.e("PaySafetyCheckManager", "start wifi scan task ...");
            }
        }
    }

    public void a(IAntiVirusServer iAntiVirusServer, int i2, d dVar) {
        synchronized (this.o) {
            if (this.g.longValue() > 0) {
                this.g = 0L;
                iAntiVirusServer.e(i2);
                dVar.f();
            }
        }
    }

    public synchronized void a(IAntiVirusServer iAntiVirusServer, c cVar) {
        try {
            cVar.b();
            if (!Build.IS_INTERNATIONAL_BUILD) {
                Log.i("PaySafetyCheckManager", "WIFI");
                c(cVar);
            }
            Log.i("PaySafetyCheckManager", "SYSTEM");
            b(cVar);
            a(cVar);
            B();
            Log.i("PaySafetyCheckManager", VirusScanModel.KEY_DEFAULT);
            this.e.a(cVar);
            a(iAntiVirusServer, (String[]) this.y.toArray(new String[this.y.size()]), this.e);
            if (!Build.IS_INTERNATIONAL_BUILD) {
                Log.i("PaySafetyCheckManager", "SIGN");
                new a(cVar, this.w).execute(new Void[0]);
            }
            Message message = new Message();
            message.what = 1;
            message.obj = cVar;
            this.C.sendMessageDelayed(message, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
        } catch (Exception e2) {
            Log.e("PaySafetyCheckManager", "Exception in active scan :", e2);
        }
        return;
    }

    public synchronized void a(IAntiVirusServer iAntiVirusServer, d dVar) {
        long longValue;
        G();
        synchronized (this.o) {
            this.g = Long.valueOf(System.currentTimeMillis());
            longValue = this.g.longValue();
        }
        try {
            dVar.b();
            if (!Build.IS_INTERNATIONAL_BUILD) {
                if (dVar.isCancelled()) {
                    dVar.d();
                    return;
                }
                Log.i("PaySafetyCheckManager", "wifi");
                long currentTimeMillis = System.currentTimeMillis();
                dVar.a(a.C0039a.WIFI);
                d(dVar);
                this.q = System.currentTimeMillis() - currentTimeMillis;
            }
            if (dVar.isCancelled()) {
                dVar.d();
                return;
            }
            Log.i("PaySafetyCheckManager", "system");
            long currentTimeMillis2 = System.currentTimeMillis();
            dVar.a(a.C0039a.SYSTEM);
            c(dVar);
            this.r = System.currentTimeMillis() - currentTimeMillis2;
            if (dVar.isCancelled()) {
                dVar.d();
                return;
            }
            Log.i("PaySafetyCheckManager", "sms auth");
            long currentTimeMillis3 = System.currentTimeMillis();
            dVar.a(a.C0039a.SMS);
            a(dVar);
            b(dVar);
            this.s = System.currentTimeMillis() - currentTimeMillis3;
            if (dVar.isCancelled()) {
                dVar.d();
                return;
            }
            Log.i("PaySafetyCheckManager", "virus");
            dVar.a(a.C0039a.APP);
            this.f1563d.a(dVar);
            a(iAntiVirusServer, (String[]) this.x.keySet().toArray(new String[this.x.size()]), this.f1563d, dVar, longValue);
            if (!Build.IS_INTERNATIONAL_BUILD) {
                if (dVar.isCancelled()) {
                    dVar.d();
                    return;
                }
                Log.i("PaySafetyCheckManager", "sign");
                this.f1562c = new b(dVar, this.v, this.B.get());
                this.f1562c.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            }
        } catch (Exception e2) {
            Log.e("PaySafetyCheckManager", "Exception : error = ", e2);
        }
        return;
    }

    public void a(String str) {
        try {
            Object a2 = b.b.o.g.d.a("PaySafetyCheckManager", Class.forName("android.app.AppGlobals"), "getPackageManager", (Class<?>[]) new Class[0], new Object[0]);
            if (b.b.o.b.a.a.a(a2, str)) {
                b.b.o.b.a.a.a(a2, str, x.e(this.f, str), (IPackageDeleteObserver) null, 999, 0);
            }
            b.b.o.b.a.a.a(this.f.getPackageManager(), str, new m(this), 0);
        } catch (Exception e2) {
            Log.e("PaySafetyCheckManager", "installSignedApp exception!", e2);
        }
    }

    public void a(boolean z2) {
        this.n = z2;
    }

    public void b() {
        this.n = false;
    }

    public void b(com.miui.antivirus.model.e eVar) {
        this.l.put(eVar.q(), eVar);
    }

    public void c() {
        this.j = null;
    }

    public void c(com.miui.antivirus.model.e eVar) {
        try {
            if (eVar.o() == f.INSTALLED_APP) {
                try {
                    Object a2 = b.b.o.g.d.a("PaySafetyCheckManager", Class.forName("android.app.AppGlobals"), "getPackageManager", (Class<?>[]) new Class[0], new Object[0]);
                    if (b.b.o.b.a.a.a(a2, eVar.m())) {
                        b.b.o.b.a.a.a(a2, eVar.m(), x.e(this.f, eVar.m()), (IPackageDeleteObserver) null, 999, 0);
                    }
                    b.b.o.b.a.a.a(this.f.getPackageManager(), eVar.m(), this.D, 0);
                } catch (Exception e2) {
                    Log.e("PaySafetyCheckManager", "cleanupVirus exception!", e2);
                }
            } else {
                b.b.c.j.k.a(eVar.q());
                MediaScannerConnection.scanFile(this.f, new String[]{eVar.q()}, (String[]) null, (MediaScannerConnection.OnScanCompletedListener) null);
            }
        } catch (Exception e3) {
            Log.e("PaySafetyCheckManager", "cleanupVirus : ", e3);
        }
    }

    public void d() {
        this.m.clear();
    }

    public void d(com.miui.antivirus.model.e eVar) {
        this.m.remove(eVar.q());
    }

    public void e() {
        this.l.clear();
    }

    public void e(com.miui.antivirus.model.e eVar) {
        this.l.remove(eVar.q());
    }

    public void f() {
        this.k = new l();
    }

    public void f(com.miui.antivirus.model.e eVar) {
        this.i = eVar;
    }

    public long g() {
        long j2 = this.t;
        long j3 = this.u;
        return j2 > j3 ? j2 : j3;
    }

    public void g(com.miui.antivirus.model.e eVar) {
        this.j = eVar;
    }

    public m h() {
        return t() > 0 ? m.DANGER : r() > 0 ? m.RISK : m.SAFE;
    }

    public int i() {
        int i2 = 0;
        int x2 = this.k.x() + t() + r() + (this.i != null ? 1 : 0) + (this.n ? 1 : 0) + (b.b.a.e.c.d(this.f) ^ true ? 1 : 0);
        if (this.j != null) {
            i2 = 1;
        }
        return x2 + i2 + (p.j() ^ true ? 1 : 0);
    }

    public com.miui.antivirus.model.e j() {
        return this.i;
    }

    public boolean k() {
        return this.n;
    }

    public m l() {
        return (this.i != null || this.n || !b.b.a.e.c.d(this.f)) ? m.DANGER : m.SAFE;
    }

    public com.miui.antivirus.model.e m() {
        return this.j;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0015, code lost:
        r0 = r1.j;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public b.b.b.d.m n() {
        /*
            r1 = this;
            com.miui.antivirus.model.e r0 = r1.j
            if (r0 == 0) goto L_0x000f
            com.miui.antivirus.model.j r0 = (com.miui.antivirus.model.j) r0
            boolean r0 = r0.y()
            if (r0 == 0) goto L_0x000f
            b.b.b.d.m r0 = b.b.b.d.m.DANGER
            return r0
        L_0x000f:
            boolean r0 = b.b.b.p.j()
            if (r0 == 0) goto L_0x0025
            com.miui.antivirus.model.e r0 = r1.j
            if (r0 == 0) goto L_0x0022
            com.miui.antivirus.model.j r0 = (com.miui.antivirus.model.j) r0
            boolean r0 = r0.x()
            if (r0 == 0) goto L_0x0022
            goto L_0x0025
        L_0x0022:
            b.b.b.d.m r0 = b.b.b.d.m.SAFE
            return r0
        L_0x0025:
            b.b.b.d.m r0 = b.b.b.d.m.RISK
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.b.o.n():b.b.b.d.m");
    }

    public long o() {
        return this.q + this.r + this.s + g();
    }

    public int p() {
        return this.z;
    }

    public m q() {
        return (x() == m.DANGER || n() == m.DANGER || l() == m.DANGER || h() == m.DANGER) ? m.DANGER : (x() == m.RISK || n() == m.RISK || h() == m.RISK) ? m.RISK : m.SAFE;
    }

    public int r() {
        return this.m.size();
    }

    public List<com.miui.antivirus.model.e> s() {
        ArrayList arrayList = new ArrayList();
        for (String str : this.m.keySet()) {
            arrayList.add(this.m.get(str));
        }
        return arrayList;
    }

    public int t() {
        return this.l.size();
    }

    public List<com.miui.antivirus.model.e> u() {
        ArrayList arrayList = new ArrayList();
        for (String str : this.l.keySet()) {
            arrayList.add(this.l.get(str));
        }
        return arrayList;
    }

    public int v() {
        int i2 = 0;
        for (String str : this.l.keySet()) {
            if (this.l.get(str).p() == g.VIRUS) {
                i2++;
            }
        }
        return i2;
    }

    public l w() {
        return this.k;
    }

    public m x() {
        return (this.k.C() || this.k.A() || this.k.y()) ? m.DANGER : (!this.k.z() || this.k.B()) ? m.SAFE : m.RISK;
    }

    public boolean y() {
        com.miui.antivirus.model.e eVar = this.j;
        return eVar != null && ((j) eVar).y();
    }

    public boolean z() {
        com.miui.antivirus.model.e eVar = this.j;
        return eVar != null && ((j) eVar).x();
    }
}
