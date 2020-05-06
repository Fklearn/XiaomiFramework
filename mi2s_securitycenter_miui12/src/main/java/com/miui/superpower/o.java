package com.miui.superpower;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import b.b.c.j.B;
import b.b.c.j.v;
import com.miui.common.persistence.b;
import com.miui.earthquakewarning.Constants;
import com.miui.luckymoney.config.AppConstants;
import com.miui.powercenter.provider.PowerSaveService;
import com.miui.powercenter.quickoptimize.C0522a;
import com.miui.securitycenter.R;
import com.miui.superpower.a.a;
import com.miui.superpower.a.c;
import com.miui.superpower.a.d;
import com.miui.superpower.a.e;
import com.miui.superpower.a.g;
import com.miui.superpower.a.h;
import com.miui.superpower.a.i;
import com.miui.superpower.a.j;
import com.miui.superpower.a.l;
import com.miui.superpower.b.k;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class o {

    /* renamed from: a  reason: collision with root package name */
    private static final List<String> f8133a = new ArrayList(4);

    /* renamed from: b  reason: collision with root package name */
    private static volatile o f8134b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public Context f8135c;

    /* renamed from: d  reason: collision with root package name */
    private HandlerThread f8136d;
    /* access modifiers changed from: private */
    public Handler e;
    private IntentFilter f;
    private Intent g;
    /* access modifiers changed from: private */
    public ResolveInfo h;
    /* access modifiers changed from: private */
    public int i;
    /* access modifiers changed from: private */
    public volatile AtomicBoolean j = new AtomicBoolean(false);
    /* access modifiers changed from: private */
    public List<d> k = new ArrayList();
    /* access modifiers changed from: private */
    public AtomicBoolean l = new AtomicBoolean(false);
    /* access modifiers changed from: private */
    public AtomicBoolean m = new AtomicBoolean(false);
    /* access modifiers changed from: private */
    public boolean n;
    /* access modifiers changed from: private */
    public boolean o;
    private NotificationManager p;
    /* access modifiers changed from: private */
    public SharedPreferences q;
    private ContentObserver r = new f(this, new Handler(Looper.getMainLooper()));
    private ContentObserver s = new h(this, new Handler(Looper.getMainLooper()));

    static {
        f8133a.add(AppConstants.Package.PACKAGE_NAME_QQ);
        f8133a.add(AppConstants.Package.PACKAGE_NAME_MM);
        f8133a.add("com.tencent.tim");
        f8133a.add("com.alibaba.android.rimet");
    }

    private o(Context context) {
        List<d> list;
        Object obj;
        this.f8135c = context.getApplicationContext();
        this.p = (NotificationManager) this.f8135c.getSystemService("notification");
        this.f8136d = new HandlerThread("SuperPowerLauncherActivity");
        this.f8136d.start();
        this.e = new Handler(this.f8136d.getLooper());
        b.a("PREF_KEY_STANDBY_4G", com.miui.superpower.b.b.a().floatValue());
        b.a("PREF_KEY_STANDBY_WIFI", com.miui.superpower.b.b.b().floatValue());
        this.f = new IntentFilter();
        this.f.addAction("android.intent.action.MAIN");
        this.f.addCategory("android.intent.category.HOME");
        this.g = new Intent("android.intent.action.MAIN");
        this.g.addCategory("android.intent.category.HOME");
        this.q = this.f8135c.getSharedPreferences("sp_superpower", 0);
        if (com.miui.powercenter.utils.o.m(this.f8135c)) {
            this.j.set(true);
            if (!TextUtils.isEmpty(this.q.getString("pref_key_superpower_origin_home_pkg", ""))) {
                this.h = new ResolveInfo();
                this.h.activityInfo = new ActivityInfo();
                this.h.activityInfo.packageName = this.q.getString("pref_key_superpower_origin_home_pkg", "");
                this.h.activityInfo.name = this.q.getString("pref_key_superpower_origin_home_act", "");
            }
        }
        this.k.add(new l(this.f8135c, this.q));
        this.k.add(new e(this.f8135c, this.q));
        this.k.add(new j(this.f8135c, this.q));
        this.k.add(new i(this.f8135c, this.q));
        if (k.a(this.f8135c)) {
            list = this.k;
            obj = new h(this.f8135c, this.q);
        } else {
            list = this.k;
            obj = new g(this.f8135c, this.q);
        }
        list.add(obj);
        this.k.add(new a(this.f8135c, this.q));
        this.k.add(new c(this.f8135c, this.q));
        this.k.add(new com.miui.superpower.a.b(this.f8135c, this.q));
        a.a(this.f8135c, this.q, this.k);
        this.i = com.miui.powercenter.utils.o.e(this.f8135c);
        this.l.set(com.miui.superpower.b.g.a());
        this.m.set(com.miui.superpower.b.g.b());
        this.n = this.q.getBoolean("pref_key_superpower_user_entersuperpower", false);
        this.o = this.q.getBoolean("pref_key_superpower_user_leavesuperpower", false);
        this.f8135c.getContentResolver().registerContentObserver(Uri.withAppendedPath(Uri.parse("content://com.miui.securitycenter.remoteprovider"), "key_superpower_autoenter"), false, this.r);
        this.f8135c.getContentResolver().registerContentObserver(Uri.withAppendedPath(Uri.parse("content://com.miui.securitycenter.remoteprovider"), "key_superpower_autoleave"), false, this.s);
        if (!this.j.get()) {
            Log.w("SuperPowerSaveManager", "constructor restore");
            this.e.post(new i(this));
        } else if (!Constants.SECURITY_ADD_PACKAGE.equals(this.f8135c.getPackageManager().resolveActivity(this.g, 0).activityInfo.packageName)) {
            Log.w("SuperPowerSaveManager", "superpower mode but launcher error");
            a(false, true);
        } else {
            Log.w("SuperPowerSaveManager", "constructor repower");
            e();
        }
    }

    public static o a(Context context) {
        if (f8134b == null) {
            synchronized (o.class) {
                if (f8134b == null) {
                    f8134b = new o(context);
                }
            }
        }
        return f8134b;
    }

    /* access modifiers changed from: private */
    public void a() {
        try {
            if (Constants.SECURITY_ADD_PACKAGE.equals(this.f8135c.getPackageManager().resolveActivity(this.g, 0).activityInfo.packageName)) {
                Log.w("SuperPowerSaveManager", "not superpower mode but home not restore");
                f();
                a(0);
            }
            this.f8135c.getPackageManager().setComponentEnabledSetting(new ComponentName(Constants.SECURITY_ADD_PACKAGE, "com.miui.superpower.SuperPowerLauncherActivity"), 2, 1);
        } catch (Exception e2) {
            Log.e("SuperPowerSaveManager", "checkoutRestoreHome exception : " + e2);
        }
    }

    /* access modifiers changed from: private */
    public void a(int i2) {
        Settings.System.putInt(this.f8135c.getContentResolver(), "power_supersave_mode_open", i2);
        if (k.b() && k.j(this.f8135c)) {
            Log.w("SuperPowerSaveManager", "setSuperSaveState xspace : " + i2);
            com.miui.support.provider.g.b(this.f8135c.getContentResolver(), "power_supersave_mode_open", i2, 999);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v6, resolved type: java.lang.Object[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void a(android.content.pm.ResolveInfo r13) {
        /*
            r12 = this;
            java.lang.String r0 = "SuperPowerSaveManager"
            android.content.Context r1 = r12.f8135c     // Catch:{ Exception -> 0x00a3 }
            android.content.pm.PackageManager r1 = r1.getPackageManager()     // Catch:{ Exception -> 0x00a3 }
            android.content.Intent r2 = r12.g     // Catch:{ Exception -> 0x00a3 }
            r3 = 131072(0x20000, float:1.83671E-40)
            java.util.List r2 = r1.queryIntentActivities(r2, r3)     // Catch:{ Exception -> 0x00a3 }
            int r3 = r2.size()     // Catch:{ Exception -> 0x00a3 }
            android.content.ComponentName[] r4 = new android.content.ComponentName[r3]     // Catch:{ Exception -> 0x00a3 }
            r5 = 0
            r6 = r5
            r7 = r6
        L_0x0019:
            if (r6 >= r3) goto L_0x005b
            java.lang.Object r8 = r2.get(r6)     // Catch:{ Exception -> 0x00a3 }
            android.content.pm.ResolveInfo r8 = (android.content.pm.ResolveInfo) r8     // Catch:{ Exception -> 0x00a3 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00a3 }
            r9.<init>()     // Catch:{ Exception -> 0x00a3 }
            java.lang.String r10 = "home component("
            r9.append(r10)     // Catch:{ Exception -> 0x00a3 }
            android.content.pm.ActivityInfo r10 = r8.activityInfo     // Catch:{ Exception -> 0x00a3 }
            java.lang.String r10 = r10.packageName     // Catch:{ Exception -> 0x00a3 }
            r9.append(r10)     // Catch:{ Exception -> 0x00a3 }
            java.lang.String r10 = ")-"
            r9.append(r10)     // Catch:{ Exception -> 0x00a3 }
            int r10 = r8.match     // Catch:{ Exception -> 0x00a3 }
            r9.append(r10)     // Catch:{ Exception -> 0x00a3 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x00a3 }
            android.util.Log.w(r0, r9)     // Catch:{ Exception -> 0x00a3 }
            android.content.ComponentName r9 = new android.content.ComponentName     // Catch:{ Exception -> 0x00a3 }
            android.content.pm.ActivityInfo r10 = r8.activityInfo     // Catch:{ Exception -> 0x00a3 }
            java.lang.String r10 = r10.packageName     // Catch:{ Exception -> 0x00a3 }
            android.content.pm.ActivityInfo r11 = r8.activityInfo     // Catch:{ Exception -> 0x00a3 }
            java.lang.String r11 = r11.name     // Catch:{ Exception -> 0x00a3 }
            r9.<init>(r10, r11)     // Catch:{ Exception -> 0x00a3 }
            r4[r6] = r9     // Catch:{ Exception -> 0x00a3 }
            int r9 = r8.match     // Catch:{ Exception -> 0x00a3 }
            if (r9 <= r7) goto L_0x0058
            int r7 = r8.match     // Catch:{ Exception -> 0x00a3 }
        L_0x0058:
            int r6 = r6 + 1
            goto L_0x0019
        L_0x005b:
            android.content.IntentFilter r2 = new android.content.IntentFilter     // Catch:{ Exception -> 0x00a3 }
            android.content.IntentFilter r3 = r12.f     // Catch:{ Exception -> 0x00a3 }
            r2.<init>(r3)     // Catch:{ Exception -> 0x00a3 }
            java.lang.String r3 = "android.intent.category.DEFAULT"
            r2.addCategory(r3)     // Catch:{ Exception -> 0x00a3 }
            java.lang.String r3 = "android.intent.category.BROWSABLE"
            r2.addCategory(r3)     // Catch:{ Exception -> 0x00a3 }
            r3 = 4
            java.lang.Class[] r6 = new java.lang.Class[r3]     // Catch:{ Exception -> 0x00a3 }
            java.lang.Class<android.content.IntentFilter> r8 = android.content.IntentFilter.class
            r6[r5] = r8     // Catch:{ Exception -> 0x00a3 }
            java.lang.Class r8 = java.lang.Integer.TYPE     // Catch:{ Exception -> 0x00a3 }
            r9 = 1
            r6[r9] = r8     // Catch:{ Exception -> 0x00a3 }
            java.lang.Class<android.content.ComponentName[]> r8 = android.content.ComponentName[].class
            r10 = 2
            r6[r10] = r8     // Catch:{ Exception -> 0x00a3 }
            java.lang.Class<android.content.ComponentName> r8 = android.content.ComponentName.class
            r11 = 3
            r6[r11] = r8     // Catch:{ Exception -> 0x00a3 }
            java.lang.String r8 = "replacePreferredActivity"
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ Exception -> 0x00a3 }
            r3[r5] = r2     // Catch:{ Exception -> 0x00a3 }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r7)     // Catch:{ Exception -> 0x00a3 }
            r3[r9] = r2     // Catch:{ Exception -> 0x00a3 }
            r3[r10] = r4     // Catch:{ Exception -> 0x00a3 }
            android.content.ComponentName r2 = new android.content.ComponentName     // Catch:{ Exception -> 0x00a3 }
            android.content.pm.ActivityInfo r4 = r13.activityInfo     // Catch:{ Exception -> 0x00a3 }
            java.lang.String r4 = r4.packageName     // Catch:{ Exception -> 0x00a3 }
            android.content.pm.ActivityInfo r13 = r13.activityInfo     // Catch:{ Exception -> 0x00a3 }
            java.lang.String r13 = r13.name     // Catch:{ Exception -> 0x00a3 }
            r2.<init>(r4, r13)     // Catch:{ Exception -> 0x00a3 }
            r3[r11] = r2     // Catch:{ Exception -> 0x00a3 }
            b.b.o.g.e.a((java.lang.Object) r1, (java.lang.String) r8, (java.lang.Class<?>[]) r6, (java.lang.Object[]) r3)     // Catch:{ Exception -> 0x00a3 }
            goto L_0x00bb
        L_0x00a3:
            r13 = move-exception
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "sethometosystem exception : "
            r1.append(r2)
            r1.append(r13)
            java.lang.String r1 = r1.toString()
            android.util.Log.e(r0, r1)
            r13.printStackTrace()
        L_0x00bb:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.superpower.o.a(android.content.pm.ResolveInfo):void");
    }

    private boolean b() {
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) this.f8135c.getSystemService("activity")).getRunningAppProcesses();
        List<String> a2 = k.a(this.f8135c.getPackageManager(), B.j(), (HashSet<String>) null);
        String b2 = k.b(this.f8135c);
        if (!(runningAppProcesses == null || runningAppProcesses.size() == 0)) {
            for (ActivityManager.RunningAppProcessInfo next : runningAppProcesses) {
                String[] strArr = next.pkgList;
                String str = strArr != null ? strArr[0] : null;
                if (!TextUtils.isEmpty(str)) {
                    try {
                        if ((this.f8135c.getPackageManager().getApplicationInfo(str, 0).flags & 1) != 0 && !a2.contains(str)) {
                        }
                    } catch (PackageManager.NameNotFoundException e2) {
                        e2.printStackTrace();
                    }
                    if ((!str.equals(this.f8135c.getPackageName()) || next.importance == 100) && ((!str.equals(b2) || next.importance == 100) && next.importance <= 125)) {
                        Log.w("SuperPowerSaveManager", "has foreground app : " + str);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public boolean b(boolean z, int i2, int i3) {
        return !this.j.get() && i2 <= 5 && i2 < i3 && !this.o && this.l.get() && !c() && !b();
    }

    private boolean c() {
        return ((TelephonyManager) this.f8135c.getSystemService("phone")).getCallState() != 0;
    }

    /* access modifiers changed from: private */
    public boolean c(boolean z, int i2, int i3) {
        return this.j.get() && z && i2 > i3 && i2 >= 50 && !this.n && this.m.get();
    }

    private void d() {
        try {
            ArrayList arrayList = new ArrayList();
            arrayList.add(this.h.activityInfo.packageName);
            int intValue = ((Integer) b.b.o.g.e.a(Class.forName("miui.process.ProcessConfig"), "KILL_LEVEL_KILL", Integer.TYPE)).intValue();
            ArrayMap arrayMap = new ArrayMap();
            arrayMap.put(Integer.valueOf(intValue), arrayList);
            int intValue2 = ((Integer) b.b.o.g.e.a(Class.forName("miui.process.ProcessConfig"), "POLICY_LOCK_SCREEN_CLEAN", Integer.TYPE)).intValue();
            Object a2 = b.b.o.g.c.a(Class.forName("miui.process.ProcessConfig"), (Class<?>[]) new Class[]{Integer.TYPE, Integer.TYPE, ArrayMap.class}, Integer.valueOf(intValue2), Integer.valueOf(B.c()), arrayMap);
            Class<?> cls = Class.forName("miui.process.ProcessManager");
            Class[] clsArr = new Class[1];
            clsArr[0] = Class.forName("miui.process.ProcessConfig");
            b.b.o.g.e.a(cls, "kill", (Class<?>[]) clsArr, a2);
        } catch (Exception e2) {
            Log.e("SuperPowerSaveManager", "kill home exception : " + e2);
        }
    }

    private void e() {
        this.e.post(new n(this));
    }

    /* access modifiers changed from: private */
    public void f() {
        ActivityInfo activityInfo;
        ResolveInfo resolveInfo = this.h;
        if (resolveInfo == null || (activityInfo = resolveInfo.activityInfo) == null || TextUtils.isEmpty(activityInfo.packageName) || TextUtils.isEmpty(this.h.activityInfo.name) || "com.miui.superpower.SuperPowerLauncherActivity".equals(this.h.activityInfo.name)) {
            Log.w("SuperPowerSaveManager", "default home invalid");
            List<ResolveInfo> queryIntentActivities = this.f8135c.getPackageManager().queryIntentActivities(this.g, 131072);
            if (queryIntentActivities != null && queryIntentActivities.size() > 0) {
                Iterator<ResolveInfo> it = queryIntentActivities.iterator();
                while (true) {
                    if (it.hasNext()) {
                        ResolveInfo next = it.next();
                        ActivityInfo activityInfo2 = next.activityInfo;
                        if (activityInfo2 != null && activityInfo2.packageName.equals("com.miui.home")) {
                            this.h = next;
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
        }
        a(this.h);
        SharedPreferences.Editor edit = this.q.edit();
        edit.putString("pref_key_superpower_origin_home_pkg", "");
        edit.putString("pref_key_superpower_origin_home_act", "");
        edit.commit();
    }

    /* access modifiers changed from: private */
    public void g() {
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses;
        try {
            if (!this.j.get()) {
                Set<String> stringSet = this.q.getStringSet("pref_key_restart_apps", new HashSet());
                if (stringSet.size() > 0 && (runningAppProcesses = ((ActivityManager) this.f8135c.getSystemService("activity")).getRunningAppProcesses()) != null && runningAppProcesses.size() > 0) {
                    for (ActivityManager.RunningAppProcessInfo next : runningAppProcesses) {
                        if (!String.valueOf(next.uid).startsWith("999") && next.pkgList != null) {
                            for (int i2 = 0; i2 < next.pkgList.length; i2++) {
                                if (stringSet.contains(next.pkgList[i2])) {
                                    stringSet.remove(next.pkgList[i2]);
                                }
                            }
                            if (stringSet.isEmpty()) {
                                break;
                            }
                        }
                    }
                }
                Log.w("SuperPowerSaveManager", "restart apps restore : " + stringSet.toString());
                if (stringSet.size() > 0) {
                    k.a((List<String>) new ArrayList(stringSet), B.j());
                    for (String str : stringSet) {
                        Intent intent = new Intent();
                        intent.setPackage(str);
                        intent.setAction("android.net.conn.CONNECTIVITY_CHANGE");
                        intent.addFlags(16777216);
                        this.f8135c.sendBroadcast(intent);
                    }
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public void h() {
        if (this.h == null) {
            this.h = a.a(this.f8135c);
        }
        ResolveInfo resolveInfo = new ResolveInfo();
        resolveInfo.activityInfo = new ActivityInfo();
        ActivityInfo activityInfo = resolveInfo.activityInfo;
        activityInfo.packageName = Constants.SECURITY_ADD_PACKAGE;
        activityInfo.name = "com.miui.superpower.SuperPowerLauncherActivity";
        a(resolveInfo);
        SharedPreferences.Editor edit = this.q.edit();
        edit.putString("pref_key_superpower_origin_home_pkg", this.h.activityInfo.packageName);
        edit.putString("pref_key_superpower_origin_home_act", this.h.activityInfo.name);
        edit.commit();
        d();
    }

    private void i() {
        Notification.Builder a2 = v.a(this.f8135c, "com.miui.securitycenter");
        a2.setSmallIcon(R.drawable.powercenter_small_icon);
        a2.setContentTitle(this.f8135c.getString(R.string.superpower_notification_new_title, new Object[]{5}));
        a2.setContentText(this.f8135c.getString(R.string.superpower_notification_summary));
        a2.setShowWhen(false);
        a2.setAutoCancel(true);
        Intent intent = new Intent(this.f8135c, SuperPowerSettings.class);
        intent.setFlags(268435456);
        a2.setContentIntent(PendingIntent.getActivity(this.f8135c, 0, intent, 0));
        Intent intent2 = new Intent(this.f8135c, PowerSaveService.class);
        intent2.setAction("com.miui.powercenter.action.ENTERSUPERPOWER_FROMNOTIFICATION");
        v.a(a2, new Notification.Action(0, this.f8135c.getString(R.string.superpower_notification_hint_enter), PendingIntent.getService(this.f8135c, 0, intent2, 0)));
        v.a(a2);
        Notification build = a2.build();
        b.b.o.a.a.a(build, true);
        v.a(this.p, "com.miui.securitycenter", this.f8135c.getResources().getString(R.string.notify_channel_name_security), 5);
        this.p.notify(R.string.notification_exit_power_save_mode, build);
    }

    /* access modifiers changed from: private */
    public void j() {
        try {
            HashSet hashSet = new HashSet(4);
            new ArrayList();
            List<String> a2 = C0522a.a(this.f8135c);
            if (a2 != null && a2.size() > 0) {
                for (String next : f8133a) {
                    if (a2.contains(next)) {
                        hashSet.add(next);
                    }
                }
            }
            if (hashSet.size() > 0) {
                List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) this.f8135c.getSystemService("activity")).getRunningAppProcesses();
                if (runningAppProcesses != null && runningAppProcesses.size() > 0) {
                    HashSet hashSet2 = new HashSet(hashSet);
                    hashSet.clear();
                    Iterator<ActivityManager.RunningAppProcessInfo> it = runningAppProcesses.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        ActivityManager.RunningAppProcessInfo next2 = it.next();
                        if (!String.valueOf(next2.uid).startsWith("999") && next2.pkgList != null) {
                            for (int i2 = 0; i2 < next2.pkgList.length; i2++) {
                                if (hashSet2.contains(next2.pkgList[i2])) {
                                    hashSet.add(next2.pkgList[i2]);
                                    hashSet2.remove(next2.pkgList[i2]);
                                }
                            }
                            if (hashSet2.isEmpty()) {
                                break;
                            }
                        }
                    }
                } else {
                    hashSet.clear();
                }
            }
            Log.w("SuperPowerSaveManager", "restart apps store : " + hashSet.toString());
            this.q.edit().putStringSet("pref_key_restart_apps", hashSet).commit();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public void a(boolean z, boolean z2) {
        this.e.post(new k(this, z, z2));
    }

    public boolean a(boolean z, int i2, int i3) {
        String str;
        this.i = i2;
        if (this.j.get() && i2 != i3) {
            Log.w("SuperPowerSaveManager", "powerStateChanged : " + i2);
        }
        if (c(z, i2, i3)) {
            Log.w("SuperPowerSaveManager", "powerStateChanged power more than 50 autoleave sp");
            this.e.post(new l(this, z, i2, i3));
        } else if (b(z, i2, i3)) {
            Log.w("SuperPowerSaveManager", "powerStateChanged power less than 10 autoenter sp");
            this.e.post(new m(this, z, i2, i3));
            return true;
        } else if (z || i2 > 5 || i3 <= 5 || this.j.get()) {
            if (this.o && i2 > 5) {
                this.o = false;
                str = "powerStateChanged reset user leave";
            } else if (this.n && i2 < 50) {
                this.n = false;
                str = "powerStateChanged reset user enter";
            } else if (i2 > 5 && i3 <= 50) {
                this.p.cancel(R.string.notification_exit_power_save_mode);
            }
            Log.w("SuperPowerSaveManager", str);
        } else {
            Log.w("SuperPowerSaveManager", "powerStateChanged show notification");
            i();
            return true;
        }
        return false;
    }
}
