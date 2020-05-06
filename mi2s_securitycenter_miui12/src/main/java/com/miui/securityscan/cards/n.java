package com.miui.securityscan.cards;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import com.miui.common.customview.AutoScrollViewPager;
import com.miui.networkassistant.config.Constants;
import com.miui.powercenter.utils.o;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import com.miui.securitycenter.h;
import com.xiaomi.stat.MiStat;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import miui.provider.ExtraSettings;

public class n {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static final Uri f7666a = ExtraSettings.Secure.getUriFor("key_garbage_danger_in_flag");
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public static final Uri f7667b = ExtraSettings.Secure.getUriFor("key_has_app_update");

    /* renamed from: c  reason: collision with root package name */
    public static final Uri f7668c = ExtraSettings.Secure.getUriFor("key_antivirus_danger");

    /* renamed from: d  reason: collision with root package name */
    public static final Uri f7669d = ExtraSettings.Secure.getUriFor("key_antivirus_safe");
    private b e;
    private a f;
    /* access modifiers changed from: private */
    public Context g;
    public boolean h = true;
    public long i;
    public int j = -1;
    public boolean k = true;
    public String l;
    public boolean m;
    public boolean n = true;
    public boolean o = true;
    public boolean p = true;
    public boolean q = true;
    public boolean r = false;
    public long s = 0;
    public boolean t = false;
    private List<AutoScrollViewPager> u;
    public List<c> v;
    public boolean w;
    /* access modifiers changed from: private */
    public Map<Integer, Drawable> x;
    /* access modifiers changed from: private */
    public Object y = new Object();
    private i z;

    private static class a extends ContentObserver {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<List<c>> f7670a;

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<n> f7671b;

        public a(n nVar, Handler handler) {
            super(handler);
            this.f7671b = new WeakReference<>(nVar);
        }

        public void a(List<c> list) {
            this.f7670a = new WeakReference<>(list);
        }

        public void onChange(boolean z, Uri uri) {
            WeakReference<List<c>> weakReference;
            if (this.f7671b != null && (weakReference = this.f7670a) != null) {
                List<c> list = (List) weakReference.get();
                n nVar = (n) this.f7671b.get();
                if (list != null && nVar != null) {
                    if (uri.equals(n.f7666a)) {
                        nVar.h = !h.h(Application.d());
                        nVar.i = h.b((Context) Application.d());
                        for (c onGarbageChange : list) {
                            onGarbageChange.onGarbageChange(nVar.h, nVar.i);
                        }
                    } else if (uri.equals(n.f7667b)) {
                        nVar.o = true;
                        for (c onAppManagerChange : list) {
                            onAppManagerChange.onAppManagerChange(nVar.o);
                        }
                    } else if (uri.equals(n.f7668c)) {
                        nVar.n = false;
                        for (c onSecurityScanChange : list) {
                            onSecurityScanChange.onSecurityScanChange(nVar.n);
                        }
                    } else if (uri.equals(n.f7669d)) {
                        nVar.n = true;
                        for (c onSecurityScanChange2 : list) {
                            onSecurityScanChange2.onSecurityScanChange(nVar.n);
                        }
                    }
                }
            }
        }
    }

    private static class b extends BroadcastReceiver {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<List<c>> f7672a;

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<n> f7673b;

        /* renamed from: c  reason: collision with root package name */
        private int f7674c;

        /* renamed from: d  reason: collision with root package name */
        private boolean f7675d;

        public b(n nVar) {
            this.f7673b = new WeakReference<>(nVar);
        }

        public void a(List<c> list) {
            this.f7672a = new WeakReference<>(list);
        }

        public void a(boolean z) {
            this.f7675d = z;
        }

        public void onReceive(Context context, Intent intent) {
            WeakReference<List<c>> weakReference;
            boolean z;
            if (this.f7673b != null && (weakReference = this.f7672a) != null) {
                List list = (List) weakReference.get();
                n nVar = (n) this.f7673b.get();
                if (list != null && nVar != null) {
                    String action = intent.getAction();
                    boolean z2 = false;
                    if ("android.intent.action.BATTERY_CHANGED".equals(action)) {
                        int intExtra = intent.getIntExtra(MiStat.Param.LEVEL, 0);
                        int intExtra2 = intent.getIntExtra("scale", 0);
                        if (intExtra2 != 0) {
                            int i = (intExtra * 100) / intExtra2;
                            z = o.k(context);
                            if (i != this.f7674c || z != this.f7675d) {
                                this.f7674c = i;
                                this.f7675d = z;
                                nVar.m = z;
                                nVar.j = i;
                                if (i > 10) {
                                    z2 = true;
                                }
                                nVar.k = z2;
                            } else {
                                return;
                            }
                        } else {
                            return;
                        }
                    } else if (Constants.App.ACTION_NETWORK_POLICY_UPDATE.equals(action)) {
                        new o(this, context, nVar, list).execute(new Void[0]);
                        return;
                    } else if ("miui.intent.action.POWER_SAVE_MODE_CHANGED".equals(action)) {
                        z = o.k(context);
                    } else {
                        return;
                    }
                    nVar.a(context, z, nVar, list);
                }
            }
        }
    }

    public interface c {
        void onAntiSpamChange(boolean z);

        void onAppManagerChange(boolean z);

        void onGarbageChange(boolean z, long j);

        void onNetworkAssistChange(boolean z, boolean z2, long j, boolean z3);

        void onPowerCenterChange(boolean z, int i, boolean z2, int i2, String str);

        void onSecurityScanChange(boolean z);
    }

    public enum d {
        GARBAGE_CLEANUP(R.string.menu_text_garbage_cleanup, R.string.menu_summary_garbage_cleanup, "#Intent;action=miui.intent.action.GARBAGE_CLEANUP;end"),
        NETWORK_ASSISTANTS(R.string.menu_text_networkassistants, R.string.menu_summary_networkassistants, "#Intent;action=miui.intent.action.NETWORKASSISTANT_ENTRANCE;end"),
        POWER_MANAGER(R.string.menu_text_power_manager, R.string.menu_summary_power_manager, "#Intent;action=miui.intent.action.POWER_MANAGER;end"),
        SECURITY_SCAN(R.string.menu_text_antivirus, R.string.menu_summary_antivirus, "#Intent;action=miui.intent.action.ANTI_VIRUS;end"),
        ANTI_SPAM(R.string.menu_text_antispam, R.string.menu_summary_antispam, "#Intent;action=miui.intent.action.SET_FIREWALL;end"),
        APP_MANAGER(R.string.app_manager_title, R.string.menu_summary_app_manager, "#Intent;action=miui.intent.action.APP_MANAGER;end");
        
        private int h;
        private int i;
        private String j;

        private d(int i2, int i3, String str) {
            this.h = i2;
            this.i = i3;
            this.j = str;
        }

        public String a() {
            return this.j;
        }

        public int b() {
            return this.h;
        }

        public int c() {
            return this.i;
        }
    }

    public n(Context context) {
        this.g = context.getApplicationContext();
        this.v = new ArrayList();
        this.x = new HashMap();
        this.u = new ArrayList();
        this.e = new b(this);
        this.f = new a(this, new Handler(Looper.getMainLooper()));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        intentFilter.addAction("miui.intent.action.POWER_SAVE_MODE_CHANGED");
        intentFilter.addAction(Constants.App.ACTION_NETWORK_POLICY_UPDATE);
        this.g.registerReceiver(this.e, intentFilter);
        this.g.getContentResolver().registerContentObserver(f7666a, false, this.f);
        this.g.getContentResolver().registerContentObserver(f7667b, false, this.f);
        this.g.getContentResolver().registerContentObserver(f7668c, false, this.f);
        this.g.getContentResolver().registerContentObserver(f7669d, false, this.f);
        this.e.a(this.v);
        this.e.a(o.k(this.g));
        this.f.a(this.v);
        this.z = new i(this.g, this);
        this.z.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        e();
    }

    /* access modifiers changed from: private */
    public void a(Context context, boolean z2, n nVar, List<c> list) {
        new m(this, z2, context, nVar, list).execute(new Void[0]);
    }

    private void e() {
        b.b.c.c.a.a.a(new l(this));
    }

    public Drawable a(int i2) {
        Drawable drawable;
        if (this.x == null) {
            return null;
        }
        synchronized (this.y) {
            drawable = this.x.get(Integer.valueOf(i2));
        }
        return drawable;
    }

    public void a(AutoScrollViewPager autoScrollViewPager) {
        if (!this.u.contains(autoScrollViewPager)) {
            this.u.add(autoScrollViewPager);
        }
    }

    public void a(c cVar) {
        if (!this.v.contains(cVar)) {
            this.v.add(cVar);
        }
    }

    public void a(c cVar, Set<String> set) {
        if (this.w && set != null && !set.isEmpty()) {
            for (String next : set) {
                if (d.GARBAGE_CLEANUP.a().equals(next)) {
                    cVar.onGarbageChange(this.h, this.i);
                } else if (d.NETWORK_ASSISTANTS.a().equals(next)) {
                    cVar.onNetworkAssistChange(this.q, this.r, this.s, this.t);
                } else if (d.POWER_MANAGER.a().equals(next)) {
                    cVar.onPowerCenterChange(this.m, this.j, this.k, 3, this.l);
                } else if (d.SECURITY_SCAN.a().equals(next)) {
                    cVar.onSecurityScanChange(this.n);
                } else if (d.APP_MANAGER.a().equals(next)) {
                    cVar.onAppManagerChange(this.o);
                } else if (d.ANTI_SPAM.a().equals(next)) {
                    cVar.onAntiSpamChange(this.p);
                }
            }
        }
    }

    public void c() {
        this.v.clear();
        this.g.unregisterReceiver(this.e);
        this.g.getContentResolver().unregisterContentObserver(this.f);
        i iVar = this.z;
        if (iVar != null) {
            iVar.cancel(true);
        }
        d();
    }

    public void d() {
        if (this.u != null) {
            for (int i2 = 0; i2 < this.u.size(); i2++) {
                this.u.get(i2).c();
            }
        }
    }
}
