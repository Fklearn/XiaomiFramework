package com.miui.antivirus.service;

import android.app.AppOpsManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import b.b.b.a.b;
import b.b.b.d.n;
import b.b.b.o;
import b.b.b.p;
import b.b.b.u;
import b.b.o.g.d;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.miui.guardprovider.b;
import com.miui.securitycenter.h;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.os.Build;
import miui.process.IActivityChangeListener;

public class GuardService extends Service {

    /* renamed from: a  reason: collision with root package name */
    private long f2872a = 0;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public IActivityChangeListener.Stub f2873b = new c(this);

    /* renamed from: c  reason: collision with root package name */
    public o.c f2874c = new d(this);
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public o f2875d;
    /* access modifiers changed from: private */
    public com.miui.guardprovider.b e;
    private InputMethodManager f;
    private c g;
    /* access modifiers changed from: private */
    public WifiManager h;
    /* access modifiers changed from: private */
    public u i;
    /* access modifiers changed from: private */
    public boolean j = false;
    private boolean k = false;
    /* access modifiers changed from: private */
    public boolean l = false;
    /* access modifiers changed from: private */
    public int m = 0;
    /* access modifiers changed from: private */
    public ArrayList<Integer> n = new ArrayList<>();
    /* access modifiers changed from: private */
    public int o = 0;
    /* access modifiers changed from: private */
    public ArrayList<String> p;
    /* access modifiers changed from: private */
    public ArrayList<String> q;
    /* access modifiers changed from: private */
    public ArrayList<String> r;
    private String s = null;
    /* access modifiers changed from: private */
    public b t = new b(this, (c) null);
    private HandlerThread u;
    private a v;
    private AppOpsManager w;
    private IBinder x;
    private String y = "";

    private static class a extends Handler {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<GuardService> f2876a;

        a(Looper looper, GuardService guardService) {
            super(looper);
            this.f2876a = new WeakReference<>(guardService);
        }

        public void handleMessage(Message message) {
            GuardService guardService = (GuardService) this.f2876a.get();
            if (guardService != null) {
                int i = message.what;
                if (i == 4) {
                    ArrayList unused = guardService.p = p.b((Context) guardService);
                    ArrayList unused2 = guardService.q = p.a((Context) guardService);
                    ArrayList unused3 = guardService.r = n.g(guardService);
                    try {
                        Class<?> cls = Class.forName("miui.process.ProcessManager");
                        Class[] clsArr = new Class[3];
                        clsArr[0] = List.class;
                        clsArr[1] = List.class;
                        clsArr[2] = Class.forName("miui.process.IActivityChangeListener");
                        d.a("GuardService", cls, "registerActivityChangeListener", (Class<?>[]) clsArr, p.b((Context) guardService), p.a((Context) guardService), guardService.f2873b);
                    } catch (Exception e) {
                        Log.e("GuardService", "registerActivityChangeListener exception!", e);
                    }
                    p.c(true);
                } else if (i == 5) {
                    try {
                        Class<?> cls2 = Class.forName("miui.process.ProcessManager");
                        Class[] clsArr2 = new Class[1];
                        clsArr2[0] = Class.forName("miui.process.IActivityChangeListener");
                        d.a("GuardService", cls2, "unregisterActivityChanageListener", (Class<?>[]) clsArr2, guardService.f2873b);
                    } catch (Exception e2) {
                        Log.e("GuardService", "unregisterActivityChanageListener exception!", e2);
                    }
                    p.c(false);
                    ArrayList unused4 = guardService.p = null;
                    ArrayList unused5 = guardService.q = null;
                    ArrayList unused6 = guardService.r = null;
                }
            }
        }
    }

    private class b extends Handler {
        private b() {
        }

        /* synthetic */ b(GuardService guardService, c cVar) {
            this();
        }

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                n.a(GuardService.this, 0, "com.miui.app.ExtraStatusBarManager.action_status_safepay", new Bundle());
            } else if (i == 2) {
                GuardService.this.a();
                GuardService.this.e.a();
            } else if (i == 3) {
                GuardService.this.e.a((b.a) new h(this));
            } else if (i == 6) {
                GuardService.this.e.a((b.a) new j(this, (WifiInfo) message.obj));
            }
        }
    }

    private class c extends BroadcastReceiver {
        private c() {
        }

        /* synthetic */ c(GuardService guardService, c cVar) {
            this();
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.net.wifi.CONFIGURED_NETWORKS_CHANGE".equals(action)) {
                if (intent.getIntExtra("changeReason", -1) == 1) {
                    GuardService.this.i.a(((WifiConfiguration) intent.getParcelableExtra("wifiConfiguration")).networkId);
                }
            } else if (!Build.IS_INTERNATIONAL_BUILD && p.p() && "android.net.wifi.STATE_CHANGE".equals(action) && p.p()) {
                if (GuardService.this.h.getWifiState() == 3) {
                    GuardService.this.t.removeMessages(6);
                    NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                    WifiInfo connectionInfo = GuardService.this.h.getConnectionInfo();
                    if (networkInfo != null && networkInfo.isConnected() && !"<unknown ssid>".equals(connectionInfo.getSSID()) && connectionInfo.getBSSID() != null && connectionInfo.getSupplicantState() == SupplicantState.COMPLETED && !GuardService.this.i.b(connectionInfo)) {
                        Message obtainMessage = GuardService.this.t.obtainMessage();
                        obtainMessage.what = 6;
                        obtainMessage.obj = connectionInfo;
                        GuardService.this.t.sendMessageDelayed(obtainMessage, 1000);
                        Log.e("GuardService", "start wifi check.");
                    }
                } else if (GuardService.this.h.getWifiState() <= 1) {
                    Log.e("GuardService", "cancel wifi check.");
                    GuardService.this.t.removeMessages(6);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void a() {
        Log.i("GuardService", "onScanFinish");
        this.j = false;
        this.o = 0;
        b.C0023b.n(b.C0023b.a(this.m));
        if (this.k) {
            int i2 = this.m;
            if (i2 != 1) {
                n.a(this, i2 > 0 ? 3 : this.l ? 4 : 2, "com.miui.app.ExtraStatusBarManager.action_status_safepay", new Bundle());
                this.t.sendEmptyMessageDelayed(1, 4000);
            }
            if (this.m > 0) {
                if (this.f2872a > 0) {
                    long currentTimeMillis = System.currentTimeMillis();
                    long j2 = this.f2872a;
                    if (currentTimeMillis - j2 < 86400000 || currentTimeMillis < j2) {
                        return;
                    }
                }
                Intent intent = new Intent(this, DialogService.class);
                intent.setAction("com.miui.safepay.SHOW_WARNING_DIALOG");
                intent.putExtra("extra_risk_priority", this.m);
                intent.putExtra("extra_risk_priority_all", this.n);
                startService(intent);
                Log.i("GuardService", "background security scan: RiskPriority = " + this.m);
            }
        }
    }

    private void a(String str) {
        this.y = str;
        a(true);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: java.lang.Object[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void a(boolean r12) {
        /*
            r11 = this;
            java.lang.String r0 = "GuardService"
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 24
            if (r1 >= r2) goto L_0x0009
            return
        L_0x0009:
            android.app.AppOpsManager r1 = r11.w     // Catch:{ Exception -> 0x0042 }
            java.lang.String r3 = "setUserRestriction"
            r4 = 4
            java.lang.Class[] r5 = new java.lang.Class[r4]     // Catch:{ Exception -> 0x0042 }
            java.lang.Class r6 = java.lang.Integer.TYPE     // Catch:{ Exception -> 0x0042 }
            r7 = 0
            r5[r7] = r6     // Catch:{ Exception -> 0x0042 }
            java.lang.Class r6 = java.lang.Boolean.TYPE     // Catch:{ Exception -> 0x0042 }
            r8 = 1
            r5[r8] = r6     // Catch:{ Exception -> 0x0042 }
            java.lang.Class<android.os.IBinder> r6 = android.os.IBinder.class
            r9 = 2
            r5[r9] = r6     // Catch:{ Exception -> 0x0042 }
            java.lang.Class<java.lang.String[]> r6 = java.lang.String[].class
            r10 = 3
            r5[r10] = r6     // Catch:{ Exception -> 0x0042 }
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ Exception -> 0x0042 }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ Exception -> 0x0042 }
            r4[r7] = r2     // Catch:{ Exception -> 0x0042 }
            java.lang.Boolean r12 = java.lang.Boolean.valueOf(r12)     // Catch:{ Exception -> 0x0042 }
            r4[r8] = r12     // Catch:{ Exception -> 0x0042 }
            android.os.IBinder r12 = r11.x     // Catch:{ Exception -> 0x0042 }
            r4[r9] = r12     // Catch:{ Exception -> 0x0042 }
            java.lang.String[] r12 = new java.lang.String[r8]     // Catch:{ Exception -> 0x0042 }
            java.lang.String r2 = r11.y     // Catch:{ Exception -> 0x0042 }
            r12[r7] = r2     // Catch:{ Exception -> 0x0042 }
            r4[r10] = r12     // Catch:{ Exception -> 0x0042 }
            b.b.o.g.d.a((java.lang.String) r0, (java.lang.Object) r1, (java.lang.String) r3, (java.lang.Class<?>[]) r5, (java.lang.Object[]) r4)     // Catch:{ Exception -> 0x0042 }
            goto L_0x0048
        L_0x0042:
            r12 = move-exception
            java.lang.String r1 = "restrictOpsWindow error"
            android.util.Log.e(r0, r1, r12)
        L_0x0048:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antivirus.service.GuardService.a(boolean):void");
    }

    /* access modifiers changed from: private */
    public void a(boolean z, String str) {
        this.k = z;
        if (z) {
            Log.i("GuardService", "进入支付环境!");
            e();
            if (p.l()) {
                f();
            }
            a(str);
            return;
        }
        Log.i("GuardService", "退出支付环境！");
        n.a(this, 0, "com.miui.app.ExtraStatusBarManager.action_status_safepay", new Bundle());
        if (p.l()) {
            d();
        }
        c();
    }

    private boolean a(Context context, String str) {
        Iterator<String> it = n.a(context).iterator();
        while (it.hasNext()) {
            if (str.contains(it.next())) {
                return true;
            }
        }
        return false;
    }

    private void b() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.net.wifi.supplicant.CONNECTION_CHANGE");
        intentFilter.addAction("android.net.wifi.supplicant.STATE_CHANGE");
        intentFilter.addAction("android.net.wifi.CONFIGURED_NETWORKS_CHANGE");
        this.g = new c(this, (c) null);
        registerReceiver(this.g, intentFilter);
    }

    private void b(boolean z) {
        if (h.i()) {
            if (z) {
                this.t.sendEmptyMessage(3);
            } else if (!this.t.hasMessages(3)) {
                this.t.sendEmptyMessageDelayed(3, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
            }
        }
    }

    private void c() {
        a(false);
        this.y = "";
    }

    private void d() {
        if (this.s != null) {
            Settings.Secure.putString(getContentResolver(), "default_input_method", this.s);
            this.s = null;
        }
    }

    static /* synthetic */ int e(GuardService guardService) {
        int i2 = guardService.o;
        guardService.o = i2 + 1;
        return i2;
    }

    private void e() {
        if (h.i() && !this.j) {
            this.e.a((b.a) new f(this));
        }
    }

    private void f() {
        boolean z;
        Boolean bool;
        try {
            String string = Settings.Secure.getString(getContentResolver(), "default_input_method");
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            Iterator<InputMethodInfo> it = this.f.getEnabledInputMethodList().iterator();
            while (true) {
                z = true;
                if (!it.hasNext()) {
                    break;
                }
                InputMethodInfo next = it.next();
                if ((1 & getPackageManager().getApplicationInfo(next.getPackageName(), 0).flags) == 0) {
                    arrayList2.add(next.getPackageName());
                } else if (!Build.IS_INTERNATIONAL_BUILD || ((bool = (Boolean) d.a("GuardService", (Object) next, "mIsAuxIme")) != null && !bool.booleanValue())) {
                    arrayList.add(next);
                }
            }
            Iterator it2 = arrayList2.iterator();
            while (true) {
                if (it2.hasNext()) {
                    if (string.contains((String) it2.next())) {
                        break;
                    }
                } else {
                    z = false;
                    break;
                }
            }
            if (string != null && z && !a((Context) this, string) && !arrayList.isEmpty()) {
                this.s = string;
                Settings.Secure.putString(getContentResolver(), "default_input_method", ((InputMethodInfo) arrayList.get(0)).getId());
            }
        } catch (PackageManager.NameNotFoundException e2) {
            Log.e("GuardService", "NameNotFoundException when switchToSystemInputMethod : ", e2);
        }
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        this.f2875d = o.a((Context) this);
        this.i = u.a((Context) this);
        this.e = com.miui.guardprovider.b.a((Context) this);
        this.f = (InputMethodManager) getSystemService("input_method");
        this.u = new HandlerThread("GuardService");
        this.u.start();
        this.v = new a(this.u.getLooper(), this);
        this.w = (AppOpsManager) getSystemService("appops");
        this.x = new Binder();
        this.h = (WifiManager) getApplicationContext().getSystemService("wifi");
        b();
    }

    public void onDestroy() {
        super.onDestroy();
        this.u.quitSafely();
        unregisterReceiver(this.g);
    }

    public int onStartCommand(Intent intent, int i2, int i3) {
        a aVar;
        int i4;
        if (intent != null) {
            if ("action_start_wifi_scan_task".equals(intent.getAction())) {
                b(intent.getBooleanExtra("build_wifi_cache_immediately", false));
            } else {
                if ("action_register_foreground_notification".equals(intent.getAction())) {
                    aVar = this.v;
                    i4 = 4;
                } else if ("action_unregister_foreground_notification".equals(intent.getAction())) {
                    aVar = this.v;
                    i4 = 5;
                } else if ("action_pay_safe_dialog_click_ignore".equals(intent.getAction())) {
                    this.f2872a = System.currentTimeMillis();
                }
                aVar.sendEmptyMessage(i4);
            }
        }
        return super.onStartCommand(intent, i2, i3);
    }
}
