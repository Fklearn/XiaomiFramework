package com.miui.gamebooster.gbservices;

import android.app.ActivityOptions;
import android.app.MiuiNotification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.j.B;
import b.b.c.j.g;
import b.b.c.j.v;
import b.b.o.g.e;
import com.miui.gamebooster.b.a.a;
import com.miui.gamebooster.m.C0383n;
import com.miui.gamebooster.m.C0384o;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.m.C0393y;
import com.miui.gamebooster.m.E;
import com.miui.gamebooster.service.GameBoxWindowManagerService;
import com.miui.gamebooster.service.IGameBoosterWindow;
import com.miui.gamebooster.service.ISecurityCenterNotificationListener;
import com.miui.gamebooster.service.NotificationListener;
import com.miui.gamebooster.service.NotificationListenerCallback;
import com.miui.gamebooster.service.r;
import com.miui.luckymoney.stats.MiStatUtil;
import com.miui.luckymoney.utils.SettingsUtil;
import com.miui.securitycenter.R;

public class x extends m implements a.C0046a {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public Context f4385a;

    /* renamed from: b  reason: collision with root package name */
    private Context f4386b;

    /* renamed from: c  reason: collision with root package name */
    private int f4387c;

    /* renamed from: d  reason: collision with root package name */
    private boolean f4388d;
    private boolean e;
    /* access modifiers changed from: private */
    public r f;
    public IGameBoosterWindow g;
    /* access modifiers changed from: private */
    public ISecurityCenterNotificationListener h;
    public StatusBarNotification i;
    private Intent j;
    private Object k = new Object();
    /* access modifiers changed from: private */
    public NotificationListenerCallback l = new t(this);
    private ServiceConnection m = new u(this);
    private ServiceConnection n = new v(this);

    private class a implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        private int f4389a;

        public a(int i) {
            this.f4389a = i;
        }

        public void run() {
            try {
                int i = this.f4389a;
                boolean z = false;
                if (i == 0) {
                    z = x.this.a(x.this.f4385a);
                    if (x.this.g != null) {
                        x.this.g.a(true, z);
                    }
                } else if (i == 1) {
                    if (x.this.g != null) {
                        x.this.g.a(false, false);
                    }
                    E.a();
                }
                Log.i("GameBoxService", "slide: status=" + this.f4389a + "\tstartFreeFrom=" + z);
            } catch (Exception e) {
                Log.e("GameBoxService", "GameBoosterUtils:" + e);
            }
        }
    }

    public x(Context context, r rVar) {
        this.f4385a = context;
        this.f = rVar;
        try {
            this.f4387c = ((Integer) e.a(Class.forName("android.app.ActivityManager"), Integer.TYPE, "getCurrentUser", (Class<?>[]) null, new Object[0])).intValue();
        } catch (Exception e2) {
            Log.e("GameBoosterReflectUtils", e2.toString());
        }
        this.f4386b = a(this.f4387c);
        this.j = new Intent(this.f4385a, GameBoxWindowManagerService.class);
        this.j.setAction("com.miui.gamebooster.service.GameBoxService");
    }

    public static int a(StatusBarNotification statusBarNotification) {
        try {
            return ((Integer) e.a((Object) statusBarNotification, Integer.TYPE, "getUid", (Class<?>[]) null, new Object[0])).intValue();
        } catch (Exception e2) {
            Log.i("GameBoosterReflectUtils", e2.toString());
            return 0;
        }
    }

    private Context a(int i2) {
        Context context = this.f4385a;
        if (i2 < 0) {
            return context;
        }
        try {
            return g.a(context, 4, new UserHandle(i2));
        } catch (Exception e2) {
            Log.i("GameBoosterReflectUtils", e2.toString());
            return context;
        }
    }

    public static boolean b(StatusBarNotification statusBarNotification) {
        if (statusBarNotification == null) {
            return false;
        }
        Bundle bundle = statusBarNotification.getNotification().extras;
        return bundle.getInt("android.progressMax", 0) != 0 || bundle.getBoolean("android.progressIndeterminate");
    }

    /* access modifiers changed from: private */
    public boolean c(StatusBarNotification statusBarNotification) {
        boolean z;
        int i2;
        if (statusBarNotification.getNotification().fullScreenIntent != null) {
            return true;
        }
        try {
            i2 = ((Integer) e.a(Class.forName("miui.util.NotificationFilterHelper"), "ENABLE", Integer.TYPE)).intValue();
            try {
                z = ((Boolean) e.a(Class.forName("android.provider.MiuiSettings$SilenceMode"), Boolean.TYPE, "showNotification", (Class<?>[]) new Class[]{Context.class}, this.f4385a)).booleanValue();
            } catch (Exception e2) {
                e = e2;
                Log.i("GameBoosterReflectUtils", e.toString());
                z = false;
                MiuiNotification a2 = C0384o.a(statusBarNotification.getNotification());
            }
        } catch (Exception e3) {
            e = e3;
            i2 = 0;
            Log.i("GameBoosterReflectUtils", e.toString());
            z = false;
            MiuiNotification a22 = C0384o.a(statusBarNotification.getNotification());
            if (a22 == null && !a22.isEnableFloat() && d(statusBarNotification) != i2 && b(statusBarNotification)) {
            }
        }
        MiuiNotification a222 = C0384o.a(statusBarNotification.getNotification());
        return a222 == null && !a222.isEnableFloat() && d(statusBarNotification) != i2 && b(statusBarNotification) && !z;
    }

    private int d(StatusBarNotification statusBarNotification) {
        String a2 = v.a(statusBarNotification.getNotification());
        MiuiNotification a3 = C0384o.a(statusBarNotification.getNotification());
        boolean z = true;
        if (!TextUtils.isEmpty(a2)) {
            try {
                Class<?> cls = Class.forName("miui.util.NotificationFilterHelper");
                Class cls2 = Integer.TYPE;
                Class[] clsArr = {Context.class, String.class, String.class, Integer.TYPE, Boolean.TYPE};
                Object[] objArr = new Object[5];
                objArr[0] = this.f4386b;
                objArr[1] = statusBarNotification.getPackageName();
                objArr[2] = a2;
                objArr[3] = Integer.valueOf(a(statusBarNotification));
                if (TextUtils.isEmpty(a3.getTargetPkg())) {
                    z = false;
                }
                objArr[4] = Boolean.valueOf(z);
                return ((Integer) e.a(cls, cls2, "getChannelFlag", (Class<?>[]) clsArr, objArr)).intValue();
            } catch (Exception e2) {
                Log.i("GameBoosterReflectUtils", e2.toString());
                return 0;
            }
        } else {
            try {
                Class<?> cls3 = Class.forName("miui.util.NotificationFilterHelper");
                Class cls4 = Integer.TYPE;
                Class[] clsArr2 = {Context.class, String.class, Integer.TYPE, Boolean.TYPE};
                Object[] objArr2 = new Object[4];
                objArr2[0] = this.f4386b;
                objArr2[1] = statusBarNotification.getPackageName();
                objArr2[2] = Integer.valueOf(a(statusBarNotification));
                if (TextUtils.isEmpty(a3.getTargetPkg())) {
                    z = false;
                }
                objArr2[3] = Boolean.valueOf(z);
                return ((Integer) e.a(cls3, cls4, "getAppFlag", (Class<?>[]) clsArr2, objArr2)).intValue();
            } catch (Exception e3) {
                Log.i("GameBoosterReflectUtils", e3.toString());
                return 0;
            }
        }
    }

    private void f() {
        com.miui.gamebooster.b.a.a.a();
        try {
            this.h.a(this.l);
        } catch (Exception e2) {
            Log.e("GameBoxService", "mNoticationListenerBinder:" + e2);
        }
        SettingsUtil.closeNotificationListener(this.f4385a, NotificationListener.class);
        SettingsUtil.closeAccessibility(this.f4385a, NotificationListener.class);
        this.f4385a.unbindService(this.n);
    }

    private void g() {
        com.miui.gamebooster.b.a.a.a((a.C0046a) this, this.f.b());
        SettingsUtil.enableNotificationListener(this.f4385a, NotificationListener.class);
        SettingsUtil.enableAccessibility(this.f4385a, NotificationListener.class);
        Context context = this.f4385a;
        g.a(context, new Intent(context, NotificationListener.class), this.n, 1, B.k());
    }

    public void a() {
        synchronized (this.k) {
            if (this.f4388d) {
                Log.i("GameBoxService", MiStatUtil.CLOSE);
                try {
                    if (this.e) {
                        this.f4385a.unbindService(this.m);
                        f();
                        this.e = false;
                    }
                } catch (Exception e2) {
                    Log.e("GameBoxService", "unbind error:" + e2);
                }
            }
        }
    }

    public boolean a(Context context) {
        StatusBarNotification statusBarNotification = this.i;
        if (statusBarNotification == null || !C0393y.a(statusBarNotification.getPackageName(), context)) {
            String string = Settings.Secure.getString(context.getContentResolver(), "gamebox_stick");
            String a2 = C0393y.a(string);
            ResolveInfo a3 = b.b.c.j.x.a(context, a2);
            if (string == null || !string.contains("/") || a2 == null || a3 == null || !b.b.c.j.x.h(context, a2)) {
                return false;
            }
            C0383n.a(context, a2, a3.activityInfo.name, R.string.gamebox_app_not_find);
            return true;
        }
        try {
            ActivityOptions activityOptions = (ActivityOptions) e.a(Class.forName("android.util.MiuiMultiWindowUtils"), ActivityOptions.class, "getActivityOptions", (Class<?>[]) new Class[]{Context.class, String.class}, context, this.i.getNotification().contentIntent.getCreatorPackage());
            PendingIntent pendingIntent = this.i.getNotification().contentIntent;
            if (activityOptions == null) {
                activityOptions = ActivityOptions.makeBasic();
            }
            pendingIntent.send((Context) null, 0, (Intent) null, (PendingIntent.OnFinished) null, (Handler) null, (String) null, activityOptions.toBundle());
            return true;
        } catch (Exception e2) {
            Log.i("GameBoosterReflectUtils", e2.toString());
            return false;
        }
    }

    public boolean b() {
        return true;
    }

    public void c() {
        synchronized (this.k) {
            if (this.f4388d) {
                Log.i("GameBoxService", "open");
                this.j.putExtra("intent_gamebooster_window_type", 1);
                this.j.putExtra("intent_gamebooster_coldstart", this.f.i());
                this.j.putExtra("intent_gamebooster_game_package", this.f.a());
                if (w.f4384a[this.f.b().ordinal()] == 1) {
                    this.j.putExtra("intent_booster_type", "intent_booster_type_game");
                }
                this.e = this.f4385a.bindService(this.j, this.m, 1);
                g();
            }
        }
    }

    public void d() {
        boolean z;
        if (C0388t.o()) {
            z = com.miui.gamebooster.c.a.w(true);
        } else {
            com.miui.gamebooster.c.a.a(this.f4385a);
            z = com.miui.gamebooster.c.a.a(true);
        }
        this.f4388d = z;
    }

    public int e() {
        return 5;
    }

    public void onSlideChanged(int i2) {
        Handler c2;
        r rVar = this.f;
        if (rVar != null && (c2 = rVar.c()) != null) {
            c2.post(new a(i2));
        }
    }
}
