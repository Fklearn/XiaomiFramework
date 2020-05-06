package com.miui.googlebase;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import b.b.c.h.j;
import b.b.c.j.v;
import b.b.o.g.d;
import com.miui.googlebase.b.c;
import com.miui.googlebase.b.f;
import com.miui.networkassistant.config.Constants;
import com.xiaomi.analytics.Actions;
import com.xiaomi.analytics.Analytics;
import com.xiaomi.analytics.Tracker;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import miui.R;
import miui.os.Build;
import miui.os.SystemProperties;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GoogleBaseAppInstallService extends Service {

    /* renamed from: a  reason: collision with root package name */
    private static final boolean f5424a = Build.IS_DEBUGGABLE;

    /* renamed from: b  reason: collision with root package name */
    public static final List<String> f5425b = new ArrayList();

    /* renamed from: c  reason: collision with root package name */
    private static int f5426c = 0;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public List<a> f5427d;
    /* access modifiers changed from: private */
    public a e;
    private String f;
    /* access modifiers changed from: private */
    public Context g;
    /* access modifiers changed from: private */
    public Handler h;
    private boolean i;
    private BroadcastReceiver j;
    private BroadcastReceiver k;
    /* access modifiers changed from: private */
    public a l;
    private String m;
    /* access modifiers changed from: private */
    public Dialog n = null;
    /* access modifiers changed from: private */
    public int o = 0;
    private Notification.Builder p;

    public static class a {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public final String f5428a;

        /* renamed from: b  reason: collision with root package name */
        private final String f5429b;

        /* renamed from: c  reason: collision with root package name */
        private final long f5430c;

        /* renamed from: d  reason: collision with root package name */
        private final String f5431d;
        /* access modifiers changed from: private */
        public final String e;
        private final String f;
        private final String g;
        private final String h;

        private a(String str, String str2, String str3, String str4) {
            this.f5428a = str;
            this.f5429b = str2;
            this.f5430c = 0;
            this.f5431d = str3;
            this.e = str4;
            this.f = null;
            this.g = null;
            this.h = null;
        }

        /* synthetic */ a(String str, String str2, String str3, String str4, c cVar) {
            this(str, str2, str3, str4);
        }

        public String a() {
            return this.f5431d;
        }

        public String b() {
            return this.f5429b;
        }

        public String c() {
            return this.e;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (this.f5428a != null) {
                sb.append("{url: ");
                sb.append(this.f5428a);
                sb.append("}, ");
            }
            if (this.f5429b != null) {
                sb.append("{hash: ");
                sb.append(this.f5429b);
                sb.append("}, ");
            }
            if (this.f5431d != null) {
                sb.append("{displayName: ");
                sb.append(this.f5431d);
                sb.append("}, ");
            }
            if (this.e != null) {
                sb.append("{packageName: ");
                sb.append(this.e);
                sb.append("}, ");
            }
            return sb.toString();
        }
    }

    static {
        f5425b.add("com.android.vending");
        f5425b.add("com.google.android.gms");
        f5425b.add("com.google.android.gsf.login");
        f5425b.add("com.google.android.gsf");
        f5425b.add("com.google.android.partnersetup");
    }

    private String a() {
        String str = SystemProperties.get("ro.product.cpu.abilist");
        if (str != null && !str.isEmpty()) {
            return str;
        }
        return SystemProperties.get("ro.product.cpu.abi") + "," + SystemProperties.get("ro.product.cpu.abi2");
    }

    /* access modifiers changed from: private */
    public synchronized void a(int i2) {
        if (i2 == 4 || i2 == 5 || i2 == 1 || i2 == 6) {
            a(this.g, 6);
        } else if (i2 != 7) {
            a(this.g, i2);
        }
        try {
            Object a2 = d.a("GoogleBaseApp", Class.forName("miui.security.ISecurityManager$Stub"), "asInterface", (Class<?>[]) new Class[]{IBinder.class}, (IBinder) d.a("GoogleBaseApp", Class.forName("android.os.ServiceManager"), "getService", (Class<?>[]) new Class[]{String.class}, "security"));
            if (a2 == null) {
                Log.d("GoogleBaseApp", "IWhetstoneActivityManager is null");
                Log.d("GoogleBaseApp", "stop service");
                stopSelf();
                return;
            }
            d.a("GoogleBaseApp", a2, "notifyAppsPreInstalled", (Class<?>[]) new Class[0], new Object[0]);
            this.i = false;
            StringBuilder sb = new StringBuilder();
            sb.append("token = ");
            sb.append(i2 == 7 ? "" : Integer.valueOf(f5426c));
            sb.append(", state = ");
            sb.append(i2);
            a(9, sb.toString());
            Log.d("GoogleBaseApp", "stop service");
            stopSelf();
            return;
        } catch (Exception unused) {
            try {
                Log.e("GoogleBaseApp", "ISecurityManager work abnormal");
                Log.d("GoogleBaseApp", "stop service");
            } catch (Throwable th) {
                Log.d("GoogleBaseApp", "stop service");
                stopSelf();
                throw th;
            }
        }
    }

    private void a(int i2, String str) {
        Tracker a2;
        if (!f.a(this.g, 0)) {
            Log.d("GoogleBaseApp", "not record analytics");
            return;
        }
        Analytics a3 = Analytics.a(this.g);
        if (a3 != null && (a2 = a3.a("securitycenter_googleappinstall")) != null) {
            Log.d("GoogleBaseApp", "install info analytics: stage = " + i2 + "; info : " + str);
            a2.a(Actions.a().b("time", System.currentTimeMillis()).b("install_stage", i2).b("install_info", str));
        }
    }

    /* access modifiers changed from: private */
    public void a(Context context, boolean z) {
        if (z) {
            int i2 = 0;
            for (a next : this.f5427d) {
                if (next != null && !com.miui.googlebase.b.d.a(this.g, next.e)) {
                    Log.d("GoogleBaseApp", "start to download : " + next.toString());
                    this.l.a(this.f + next.f5428a, next);
                    i2++;
                }
            }
            if (this.e != null && !f() && !com.miui.googlebase.b.d.a(this.g, this.e.e)) {
                Log.d("GoogleBaseApp", "start to download first: " + this.e.toString());
                this.l.a(this.f + this.e.f5428a, this.e);
                i2++;
            }
            if (i2 == 0) {
                a(0);
            }
        } else if (this.e != null && !f() && !com.miui.googlebase.b.d.a(this.g, this.e.e)) {
            Log.d("GoogleBaseApp", "start to download second: " + this.e.toString());
            this.l.a(this.f + this.e.f5428a, this.e);
        }
    }

    private void a(String str) {
        this.h.sendEmptyMessageDelayed(1, (long) 300000);
        h();
        this.n = new AlertDialog.Builder(this.g, R.style.Theme_Light_Dialog_Alert).setMessage(getString(com.miui.securitycenter.R.string.google_base_app_install_choice)).setPositiveButton(17039370, new g(this)).setNegativeButton(17039360, new f(this)).create();
        this.n.getWindow().setType(2003);
        this.n.getWindow().setFlags(131072, 131072);
        this.n.getWindow().setAttributes(this.n.getWindow().getAttributes());
        this.n.setCancelable(false);
        this.n.show();
    }

    private void a(String str, boolean z, boolean z2) {
        if (z2) {
            this.p.setStyle(new Notification.BigTextStyle().bigText(str));
        }
        if (z) {
            this.p.setContentText(str).setAutoCancel(true).setOngoing(false).setProgress(0, 0, false);
            return;
        }
        this.p.setContentText(str).setAutoCancel(false).setOngoing(true);
        int i2 = this.o;
        if (i2 > 0) {
            this.p.setProgress(100, i2, false);
        }
    }

    private int b() {
        return android.os.Build.MODEL.toUpperCase().indexOf("MI PAD") >= 0 ? 1 : 0;
    }

    /* access modifiers changed from: private */
    public boolean c() {
        String g2 = g();
        if (f5424a) {
            Log.d("GoogleBaseApp", "json URL: " + g2);
        }
        String a2 = c.a(g2, new j("googlebase_googlebaseappinstallservice"));
        if (a2 == null || a2.isEmpty()) {
            Log.d("GoogleBaseApp", "json is null or empty");
            return false;
        }
        try {
            JSONObject jSONObject = new JSONObject(a2);
            if (f5424a) {
                Log.d("GoogleBaseApp", "get google base app info from server - " + jSONObject);
            }
            JSONArray jSONArray = jSONObject.getJSONArray("googleSFApps");
            for (int length = jSONArray.length() - 1; length >= 0; length--) {
                JSONObject optJSONObject = jSONArray.optJSONObject(length);
                String string = optJSONObject.getString("apk");
                String string2 = optJSONObject.getString("apkHash");
                String string3 = optJSONObject.getString("displayName");
                String string4 = optJSONObject.getString("packageName");
                a aVar = new a(string, string2, string3, string4, (c) null);
                if (string4 != null) {
                    if (b.a(string4)) {
                        Log.d("GoogleBaseApp", "[" + length + "]:" + aVar.toString());
                        this.f5427d.add(aVar);
                    }
                }
                if (string4 != null && string4.equals("com.android.vending")) {
                    Log.d("GoogleBaseApp", "[" + length + "]:" + aVar.toString());
                    this.e = aVar;
                }
            }
            this.f = jSONObject.optString("host");
            if (f5424a) {
                Log.d("GoogleBaseApp", "host : " + this.f);
            }
            return true;
        } catch (JSONException e2) {
            Log.e("GoogleBaseApp", "getGoogleBaseAppInfoAndHost", e2);
            return false;
        }
    }

    private static synchronized void d() {
        synchronized (GoogleBaseAppInstallService.class) {
            f5426c++;
        }
    }

    private boolean e() {
        String str = SystemProperties.get("ro.product.cpu.abilist");
        if (str != null) {
            return str.contains("arm64") || str.contains("x86_64");
        }
        return false;
    }

    /* access modifiers changed from: private */
    public boolean f() {
        String str = this.m;
        return str != null && str.equals("com.android.vending");
    }

    private String g() {
        return "https://a0.app.xiaomi.com/gapp/v2?sdk=" + Build.VERSION.SDK_INT + "&" + "co=" + Locale.getDefault().getCountry() + "&" + "la=" + Locale.getDefault().getLanguage() + "&" + "cpu64=" + e() + "&" + "cpuArchitecture=" + a() + "&" + "deviceType=" + b() + "&" + "ref=" + "misystem";
    }

    /* access modifiers changed from: private */
    public void h() {
        Dialog dialog = this.n;
        if (dialog != null) {
            dialog.dismiss();
            this.n = null;
        }
    }

    private void i() {
        new Thread(new h(this)).start();
    }

    /* access modifiers changed from: private */
    public void j() {
        if (!this.i) {
            this.i = true;
            d();
            if (f5424a) {
                Log.d("GoogleBaseApp", "timeout = " + 300000);
            }
            if (this.h.hasMessages(1)) {
                this.h.removeMessages(1);
            }
            this.h.sendEmptyMessageDelayed(1, (long) 300000);
            i();
            a(8, "token = " + f5426c);
            return;
        }
        Log.d("GoogleBaseApp", "already begin working, deny this request");
    }

    public void a(Context context, int i2) {
        int i3;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        if (this.p == null) {
            v.a(notificationManager, "GMSChannel", "GMSNotification", 5);
            this.p = v.a(context, "GMSChannel");
            this.p.setSmallIcon(com.miui.securitycenter.R.drawable.security_small_icon).setContentTitle(getString(com.miui.securitycenter.R.string.google_base_app_title));
        }
        if (i2 != 0) {
            if (i2 == 6) {
                i3 = com.miui.securitycenter.R.string.google_base_app_fail;
            } else if (i2 == 8 || i2 == 10) {
                a(getString(com.miui.securitycenter.R.string.google_base_app_installing), false, false);
            } else if (i2 == 2) {
                i3 = com.miui.securitycenter.R.string.google_base_app_network_anomaly;
            } else if (i2 == 3) {
                i3 = com.miui.securitycenter.R.string.google_base_app_timeout;
            }
            a(getString(i3), true, true);
        } else {
            a(getString(com.miui.securitycenter.R.string.google_base_app_installed), true, false);
        }
        notificationManager.notify(1111, this.p.build());
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        Log.d("GoogleBaseApp", "created");
        this.f5427d = new ArrayList();
        this.g = this;
        this.i = false;
        this.l = new a(this.g);
        this.h = new Handler(new c(this));
        this.j = new d(this);
        this.g.registerReceiver(this.j, new IntentFilter("android.intent.action.DOWNLOAD_COMPLETE"));
        this.k = new e(this);
        IntentFilter intentFilter = new IntentFilter(Constants.System.ACTION_PACKAGE_ADDED);
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        intentFilter.addDataScheme("package");
        this.g.registerReceiver(this.k, intentFilter);
    }

    public void onDestroy() {
        super.onDestroy();
        this.g.unregisterReceiver(this.k);
        this.g.unregisterReceiver(this.j);
        this.h.removeMessages(1);
        Log.d("GoogleBaseApp", "destroy");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:28:0x008e, code lost:
        return 0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized int onStartCommand(android.content.Intent r3, int r4, int r5) {
        /*
            r2 = this;
            monitor-enter(r2)
            r4 = 0
            if (r3 == 0) goto L_0x008d
            java.lang.String r5 = "GoogleBaseApp"
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x008a }
            r0.<init>()     // Catch:{ all -> 0x008a }
            java.lang.String r1 = "start action: "
            r0.append(r1)     // Catch:{ all -> 0x008a }
            java.lang.String r1 = r3.getAction()     // Catch:{ all -> 0x008a }
            r0.append(r1)     // Catch:{ all -> 0x008a }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x008a }
            android.util.Log.d(r5, r0)     // Catch:{ all -> 0x008a }
            java.lang.String r5 = "finish"
            java.lang.String r0 = r3.getAction()     // Catch:{ all -> 0x008a }
            boolean r5 = r5.equals(r0)     // Catch:{ all -> 0x008a }
            if (r5 == 0) goto L_0x0038
            java.lang.String r5 = "state"
            int r3 = r3.getIntExtra(r5, r4)     // Catch:{ all -> 0x008a }
            r2.a((int) r3)     // Catch:{ all -> 0x008a }
            r2.stopSelf()     // Catch:{ all -> 0x008a }
            monitor-exit(r2)
            return r4
        L_0x0038:
            java.lang.String r5 = "install"
            java.lang.String r0 = r3.getAction()     // Catch:{ all -> 0x008a }
            boolean r5 = r5.equals(r0)     // Catch:{ all -> 0x008a }
            if (r5 == 0) goto L_0x008d
            boolean r5 = r2.i     // Catch:{ all -> 0x008a }
            if (r5 == 0) goto L_0x005c
            android.content.Context r3 = r2.g     // Catch:{ all -> 0x008a }
            android.content.Context r5 = r2.g     // Catch:{ all -> 0x008a }
            r0 = 2131756513(0x7f1005e1, float:1.9143936E38)
            java.lang.String r5 = r5.getString(r0)     // Catch:{ all -> 0x008a }
            android.widget.Toast r3 = android.widget.Toast.makeText(r3, r5, r4)     // Catch:{ all -> 0x008a }
            r3.show()     // Catch:{ all -> 0x008a }
            monitor-exit(r2)
            return r4
        L_0x005c:
            java.lang.String r5 = r2.m     // Catch:{ all -> 0x008a }
            if (r5 != 0) goto L_0x0080
            java.lang.String r5 = "packageName"
            java.lang.String r5 = r3.getStringExtra(r5)     // Catch:{ all -> 0x008a }
            r2.m = r5     // Catch:{ all -> 0x008a }
            java.lang.String r5 = "GoogleBaseApp"
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x008a }
            r0.<init>()     // Catch:{ all -> 0x008a }
            java.lang.String r1 = "user Installing: "
            r0.append(r1)     // Catch:{ all -> 0x008a }
            java.lang.String r1 = r2.m     // Catch:{ all -> 0x008a }
            r0.append(r1)     // Catch:{ all -> 0x008a }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x008a }
            android.util.Log.d(r5, r0)     // Catch:{ all -> 0x008a }
        L_0x0080:
            java.lang.String r5 = "appName"
            java.lang.String r3 = r3.getStringExtra(r5)     // Catch:{ all -> 0x008a }
            r2.a((java.lang.String) r3)     // Catch:{ all -> 0x008a }
            goto L_0x008d
        L_0x008a:
            r3 = move-exception
            monitor-exit(r2)
            throw r3
        L_0x008d:
            monitor-exit(r2)
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.googlebase.GoogleBaseAppInstallService.onStartCommand(android.content.Intent, int, int):int");
    }
}
