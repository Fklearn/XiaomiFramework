package com.miui.superpower.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import com.miui.common.persistence.b;
import com.miui.gamebooster.service.ISecurityCenterNotificationListener;
import com.miui.gamebooster.service.NotificationListener;
import com.miui.gamebooster.service.NotificationListenerCallback;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class f {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public Context f8126a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public Set<String> f8127b = new HashSet();
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public HashMap<String, Integer> f8128c = new HashMap<>();

    /* renamed from: d  reason: collision with root package name */
    private SharedPreferences f8129d;
    private HandlerThread e;
    /* access modifiers changed from: private */
    public Handler f;
    private long g = 0;
    /* access modifiers changed from: private */
    public ServiceConnection h;
    /* access modifiers changed from: private */
    public ISecurityCenterNotificationListener i;
    /* access modifiers changed from: private */
    public AtomicBoolean j = new AtomicBoolean(false);
    private ContentObserver k = new a(this, new Handler(Looper.getMainLooper()));
    private ContentObserver l = new b(this, new Handler(Looper.getMainLooper()));
    private BroadcastReceiver m = new c(this);
    /* access modifiers changed from: private */
    public NotificationListenerCallback n = new e(this);

    private class a extends Handler {
        public a(Looper looper) {
            super(looper);
        }

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void handleMessage(android.os.Message r4) {
            /*
                r3 = this;
                int r0 = r4.what
                java.lang.String r1 = "mNoticationListenerBinder:"
                java.lang.String r2 = "SuperPowerSaveManager"
                switch(r0) {
                    case 1: goto L_0x00fa;
                    case 2: goto L_0x00e6;
                    case 3: goto L_0x009a;
                    case 4: goto L_0x00fa;
                    case 5: goto L_0x007c;
                    case 6: goto L_0x004d;
                    case 7: goto L_0x000b;
                    default: goto L_0x0009;
                }
            L_0x0009:
                goto L_0x0104
            L_0x000b:
                com.miui.superpower.notification.f r4 = com.miui.superpower.notification.f.this     // Catch:{ Exception -> 0x0032 }
                com.miui.gamebooster.service.ISecurityCenterNotificationListener r4 = r4.i     // Catch:{ Exception -> 0x0032 }
                if (r4 == 0) goto L_0x0045
                com.miui.superpower.notification.f r4 = com.miui.superpower.notification.f.this     // Catch:{ Exception -> 0x0032 }
                com.miui.gamebooster.service.ISecurityCenterNotificationListener r4 = r4.i     // Catch:{ Exception -> 0x0032 }
                com.miui.superpower.notification.f r0 = com.miui.superpower.notification.f.this     // Catch:{ Exception -> 0x0032 }
                com.miui.gamebooster.service.NotificationListenerCallback r0 = r0.n     // Catch:{ Exception -> 0x0032 }
                r4.a(r0)     // Catch:{ Exception -> 0x0032 }
                com.miui.superpower.notification.f r4 = com.miui.superpower.notification.f.this     // Catch:{ Exception -> 0x0032 }
                android.content.Context r4 = r4.f8126a     // Catch:{ Exception -> 0x0032 }
                com.miui.superpower.notification.f r0 = com.miui.superpower.notification.f.this     // Catch:{ Exception -> 0x0032 }
                android.content.ServiceConnection r0 = r0.h     // Catch:{ Exception -> 0x0032 }
                r4.unbindService(r0)     // Catch:{ Exception -> 0x0032 }
                goto L_0x0045
            L_0x0032:
                r4 = move-exception
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                r0.append(r1)
                r0.append(r4)
                java.lang.String r4 = r0.toString()
                android.util.Log.e(r2, r4)
            L_0x0045:
                com.miui.superpower.notification.f r4 = com.miui.superpower.notification.f.this
                r0 = 0
                com.miui.gamebooster.service.ISecurityCenterNotificationListener unused = r4.i = r0
                goto L_0x0104
            L_0x004d:
                com.miui.superpower.notification.f r0 = com.miui.superpower.notification.f.this
                java.lang.Object r4 = r4.obj
                com.miui.gamebooster.service.ISecurityCenterNotificationListener r4 = (com.miui.gamebooster.service.ISecurityCenterNotificationListener) r4
                com.miui.gamebooster.service.ISecurityCenterNotificationListener unused = r0.i = r4
                com.miui.superpower.notification.f r4 = com.miui.superpower.notification.f.this     // Catch:{ Exception -> 0x0067 }
                com.miui.gamebooster.service.ISecurityCenterNotificationListener r4 = r4.i     // Catch:{ Exception -> 0x0067 }
                com.miui.superpower.notification.f r0 = com.miui.superpower.notification.f.this     // Catch:{ Exception -> 0x0067 }
                com.miui.gamebooster.service.NotificationListenerCallback r0 = r0.n     // Catch:{ Exception -> 0x0067 }
                r4.b(r0)     // Catch:{ Exception -> 0x0067 }
                goto L_0x0104
            L_0x0067:
                r4 = move-exception
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                r0.append(r1)
                r0.append(r4)
                java.lang.String r4 = r0.toString()
                android.util.Log.e(r2, r4)
                goto L_0x0104
            L_0x007c:
                java.lang.Object r4 = r4.obj
                java.lang.String r4 = (java.lang.String) r4
                boolean r0 = android.text.TextUtils.isEmpty(r4)
                if (r0 != 0) goto L_0x0094
                com.miui.superpower.notification.f r0 = com.miui.superpower.notification.f.this
                java.util.HashMap r0 = r0.f8128c
                r1 = 0
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                r0.put(r4, r1)
            L_0x0094:
                com.miui.superpower.notification.f r4 = com.miui.superpower.notification.f.this
                r4.f()
                goto L_0x00e0
            L_0x009a:
                java.lang.Object r4 = r4.obj
                java.lang.String r4 = (java.lang.String) r4
                boolean r0 = android.text.TextUtils.isEmpty(r4)
                if (r0 != 0) goto L_0x00e0
                com.miui.superpower.notification.f r0 = com.miui.superpower.notification.f.this
                java.util.Set r0 = r0.f8127b
                boolean r0 = r0.contains(r4)
                if (r0 == 0) goto L_0x00e0
                com.miui.superpower.notification.f r0 = com.miui.superpower.notification.f.this
                java.util.HashMap r0 = r0.f8128c
                java.lang.Object r0 = r0.get(r4)
                java.lang.Integer r0 = (java.lang.Integer) r0
                r1 = 1
                if (r0 != 0) goto L_0x00cd
                com.miui.superpower.notification.f r0 = com.miui.superpower.notification.f.this
                java.util.HashMap r0 = r0.f8128c
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                r0.put(r4, r1)
                goto L_0x0094
            L_0x00cd:
                com.miui.superpower.notification.f r2 = com.miui.superpower.notification.f.this
                java.util.HashMap r2 = r2.f8128c
                int r0 = r0.intValue()
                int r0 = r0 + r1
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                r2.put(r4, r0)
                goto L_0x0094
            L_0x00e0:
                com.miui.superpower.notification.f r4 = com.miui.superpower.notification.f.this
                r4.e()
                goto L_0x0104
            L_0x00e6:
                com.miui.superpower.notification.f r4 = com.miui.superpower.notification.f.this
                java.util.HashMap r4 = r4.f8128c
                r4.clear()
                com.miui.superpower.notification.f r4 = com.miui.superpower.notification.f.this
                r4.f()
                com.miui.superpower.notification.f r4 = com.miui.superpower.notification.f.this
                r4.b()
                goto L_0x0104
            L_0x00fa:
                com.miui.superpower.notification.f r4 = com.miui.superpower.notification.f.this
                r4.c()
                com.miui.superpower.notification.f r4 = com.miui.superpower.notification.f.this
                r4.d()
            L_0x0104:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.superpower.notification.f.a.handleMessage(android.os.Message):void");
        }
    }

    public f(Context context) {
        this.f8126a = context;
        this.f8129d = this.f8126a.getSharedPreferences("sp_superpower_appmsgnum", 0);
    }

    /* access modifiers changed from: private */
    public void a(StatusBarNotification statusBarNotification) {
        if (statusBarNotification != null) {
            long j2 = this.g;
            if ((j2 != 0 && j2 == statusBarNotification.getPostTime()) || !statusBarNotification.isClearable()) {
                return;
            }
            if (!"com.android.mms".equals(statusBarNotification.getPackageName()) || (statusBarNotification.getNotification() != null && !TextUtils.isEmpty(statusBarNotification.getNotification().tickerText))) {
                this.g = statusBarNotification.getPostTime();
                String packageName = "com.android.server.telecom".equals(statusBarNotification.getPackageName()) ? "com.android.contacts" : statusBarNotification.getPackageName();
                if (!(Build.VERSION.SDK_INT < 21 || statusBarNotification.getUser() == null || statusBarNotification.getUser().getIdentifier() == 0 || packageName == null)) {
                    packageName = packageName.concat(":999");
                }
                if (!com.miui.superpower.a.a(statusBarNotification)) {
                    Message obtainMessage = this.f.obtainMessage(3);
                    obtainMessage.obj = packageName;
                    this.f.sendMessage(obtainMessage);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void c() {
        this.f8127b.clear();
        String a2 = b.a("pref_key_superpower_power_save_app", "");
        if (!TextUtils.isEmpty(a2)) {
            try {
                JSONArray jSONArray = new JSONArray(a2);
                for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                    this.f8127b.add(jSONArray.optString(i2));
                }
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
        this.f8127b.add("com.android.mms");
        this.f8127b.add("com.android.contacts");
    }

    /* access modifiers changed from: private */
    public void d() {
        String string = this.f8129d.getString("key_appmsgnum", (String) null);
        if (!TextUtils.isEmpty(string)) {
            try {
                JSONArray jSONArray = new JSONArray(string);
                if (jSONArray.length() > 0) {
                    for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                        JSONObject jSONObject = jSONArray.getJSONObject(i2);
                        String string2 = jSONObject.getString("pkg_name");
                        if (this.f8127b.contains(string2)) {
                            this.f8128c.put(string2, Integer.valueOf(jSONObject.getInt("pkg_appmsgnum")));
                        }
                    }
                }
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: private */
    public void e() {
        try {
            b.b("PREF_KEY_APPMSG", new JSONObject(this.f8128c).toString());
        } catch (Exception e2) {
            Log.e("SuperPowerSaveManager", e2.toString());
        }
    }

    /* access modifiers changed from: private */
    public void f() {
        JSONArray jSONArray = new JSONArray();
        for (String next : this.f8128c.keySet()) {
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("pkg_name", next);
                jSONObject.put("pkg_appmsgnum", this.f8128c.get(next));
                jSONArray.put(jSONObject);
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
        this.f8129d.edit().putString("key_appmsgnum", jSONArray.toString()).commit();
    }

    public void a() {
        this.j.set(false);
        this.e = new HandlerThread("SuperPowerLauncherActivity");
        this.e.start();
        this.f = new a(this.e.getLooper());
        this.f.sendEmptyMessage(1);
        this.f8126a.getContentResolver().registerContentObserver(Settings.System.getUriFor("power_supersave_mode_open"), false, this.k);
        this.f8126a.getContentResolver().registerContentObserver(Uri.withAppendedPath(Uri.parse("content://com.miui.securitycenter.remoteprovider"), "pref_key_superpower_power_save_app"), false, this.l);
        this.f8126a.registerReceiver(this.m, new IntentFilter("com.miui.securityadd.superpower.APP_CLICK_ACTION"));
        Log.d("SuperPowerSaveManager", "super power notifyservice onCreate");
        this.h = new d(this);
        Context context = this.f8126a;
        context.bindService(new Intent(context, NotificationListener.class), this.h, 1);
    }

    public void b() {
        if (!this.j.get()) {
            this.j.set(true);
            this.f.sendEmptyMessage(7);
            this.f8126a.getContentResolver().unregisterContentObserver(this.k);
            this.f8126a.getContentResolver().unregisterContentObserver(this.l);
            this.f8126a.unregisterReceiver(this.m);
            this.e.quitSafely();
            Log.d("SuperPowerSaveManager", "super power notifyservice onDestroy");
        }
    }
}
