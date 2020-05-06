package com.miui.powercenter.provider;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.j.B;
import b.b.c.j.g;
import b.b.c.j.v;
import b.b.o.a.a;
import com.miui.networkassistant.config.Constants;
import com.miui.optimizemanage.settings.d;
import com.miui.powercenter.PowerCenter;
import com.miui.powercenter.autotask.AutoTask;
import com.miui.powercenter.autotask.C0477f;
import com.miui.powercenter.autotask.C0479h;
import com.miui.powercenter.batteryhistory.C0501e;
import com.miui.powercenter.batteryhistory.C0514s;
import com.miui.powercenter.batteryhistory.C0516u;
import com.miui.powercenter.batteryhistory.C0520y;
import com.miui.powercenter.batteryhistory.aa;
import com.miui.powercenter.powersaver.b;
import com.miui.powercenter.powerui.h;
import com.miui.powercenter.quickoptimize.C0530i;
import com.miui.powercenter.utils.j;
import com.miui.powercenter.utils.l;
import com.miui.powercenter.utils.o;
import com.miui.powercenter.utils.s;
import com.miui.powercenter.y;
import com.miui.securitycenter.R;
import com.miui.securityscan.M;
import com.miui.superpower.b.k;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.io.IoUtils;

public class PowerSaveService extends Service {

    /* renamed from: a  reason: collision with root package name */
    private d f7163a;

    /* renamed from: b  reason: collision with root package name */
    private C0479h f7164b;

    /* renamed from: c  reason: collision with root package name */
    private C0477f f7165c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public h f7166d;
    /* access modifiers changed from: private */
    public boolean e = false;
    /* access modifiers changed from: private */
    public boolean f = false;
    /* access modifiers changed from: private */
    public ArrayList<String> g = new ArrayList<>();
    private final BroadcastReceiver h = new f(this);

    /* access modifiers changed from: private */
    public void a() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setAction("com.miui.powercenter.action.DISABLE_MOBILE_DATA");
        PendingIntent b2 = g.b((Context) this, 0, intent);
        if (b2 != null) {
            ((AlarmManager) getSystemService("alarm")).cancel(b2);
        }
    }

    /* access modifiers changed from: private */
    public void a(Context context, int i) {
        int b2;
        if (!o.m(context)) {
            if (((b.a() < 60 && i >= 60) || i >= 100) && a(context) && (b2 = b.b()) < 3) {
                b(context, i);
                b.b(b2 + 1);
            }
        }
    }

    private void a(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("Power save mode on: " + o.l(this));
        printWriter.println("Mobile data on lock screen time: " + y.i());
        printWriter.println("Memory clean on lock screen time: " + y.l());
        printWriter.println("Battery consume abnormal: " + y.x());
        printWriter.println("Battery overheat value: " + y.e());
        printWriter.println("Memory Clean cloud white list size: " + this.g.size());
        printWriter.println("Show exit save mode count: " + b.b());
        printWriter.println("Auto exit power save mode enabled: " + y.a());
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = this.g.iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            sb.append(",");
        }
        printWriter.println(sb.toString());
        printWriter.println("No kill pkg ver:" + M.g());
        printWriter.println();
        printWriter.println("Save mode: ");
        int a2 = b.a();
        printWriter.println("saveModeStartPercent: " + a2);
        boolean u = y.u();
        printWriter.println("Save mode alarm: " + u);
        if (u) {
            int w = y.w();
            printWriter.println("Open time: " + (w / 60) + ":" + (w % 60));
            int v = y.v();
            printWriter.println("Close time: " + (v / 60) + ":" + (v % 60));
        }
        printWriter.println();
        boolean m = y.m();
        printWriter.println("Boot on time: " + m);
        if (m) {
            int o = y.o();
            printWriter.println("Boot time " + s.a(o / 60, o % 60));
            printWriter.println("Boot saved time " + s.b(y.p()));
            printWriter.println("Boot repeat type " + y.n());
        }
        boolean r = y.r();
        printWriter.println("Shutdown on time: " + r);
        if (r) {
            int t = y.t();
            printWriter.println("Shutdown time " + s.a(t / 60, t % 60));
            printWriter.println("Shutdown saved time " + s.b(y.q()));
            printWriter.println("Shutdown repeat type " + y.s());
        }
        printWriter.println(TtmlNode.END);
        Cursor query = getContentResolver().query(AutoTask.CONTENT_URI, AutoTask.QUERY_COLUMNS, (String) null, (String[]) null, (String) null);
        printWriter.println("Auto task list all:");
        if (query != null) {
            try {
                if (query.moveToFirst()) {
                    do {
                        AutoTask autoTask = new AutoTask(query);
                        printWriter.println("Task id " + autoTask.getId() + ", enabled " + autoTask.getEnabled() + ", repeated type " + autoTask.getRepeatType() + ", started " + autoTask.getStarted() + ", condition " + autoTask.getConditionString() + ", operation " + autoTask.getOperationString() + ", restore operation " + autoTask.getRestoreOperationString() + ", restore level " + autoTask.getRestoreLevel() + ", period task " + autoTask.isPeriodTask());
                    } while (query.moveToNext());
                }
            } finally {
                IoUtils.closeQuietly(query);
            }
        }
        printWriter.println(TtmlNode.END);
        printWriter.println("Battery info all:");
        boolean k = o.k(this);
        printWriter.println("charging: " + k);
        List<aa> b2 = C0514s.c().b();
        if (k) {
            C0501e.a a3 = C0501e.a((Context) this, b2);
            printWriter.println("leftChargeTime: " + s.a(a3.f6879a));
            printWriter.println("totalChargedTime: " + s.a(a3.f6880b));
            printWriter.println("chargedTime: " + s.a(a3.f6881c));
            printWriter.println("startLevel: " + a3.f6882d);
            printWriter.println("endLevel: " + a3.e);
            printWriter.println("useMaxOrMin: " + a3.f);
            printWriter.println("useDefault: " + a3.h);
        }
        long a4 = C0520y.a(this, b2);
        printWriter.println("battery_endurance_time " + s.a(a4));
        printWriter.println("lastChargedTime: " + s.b(y.b()));
        printWriter.println("last_drained_time " + s.a(y.d()));
        printWriter.println("last_drained_percent " + y.c());
        Uri parse = Uri.parse("content://com.miui.powercenter.provider");
        Bundle call = getContentResolver().call(parse, "getBatteryCurrent", (String) null, (Bundle) null);
        if (call != null) {
            printWriter.println("current now: " + call.getInt("current_now"));
        }
        Bundle call2 = getContentResolver().call(parse, "getPowerSupplyInfo", (String) null, (Bundle) null);
        if (call2 != null) {
            printWriter.println("quick charge: " + call2.getBoolean("quick_charge"));
            String a5 = o.a();
            if (!TextUtils.isEmpty(a5)) {
                printWriter.println("charge type: " + a5);
            }
        }
        printWriter.println(TtmlNode.END);
        printWriter.println();
        printWriter.println("History charge time");
        long f2 = y.f();
        printWriter.println("ac: " + s.d(this, f2));
        long g2 = y.g();
        printWriter.println("usb: " + s.d(this, g2));
        printWriter.println(TtmlNode.END);
        printWriter.println();
    }

    private boolean a(Context context) {
        if (!o.l(context)) {
            return false;
        }
        o.a(context, false);
        return true;
    }

    /* access modifiers changed from: private */
    public void b() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setAction("com.miui.powercenter.action.CLEAN_MEMORY");
        PendingIntent b2 = g.b((Context) this, B.j(), intent);
        if (b2 != null) {
            ((AlarmManager) getSystemService("alarm")).cancel(b2);
        }
    }

    private void b(Context context, int i) {
        Notification.Builder a2 = v.a(context, "com.miui.securitycenter");
        a2.setSmallIcon(R.drawable.powercenter_small_icon);
        a2.setContentTitle(context.getString(R.string.notification_exit_power_save_mode));
        String string = context.getString(R.string.notification_exit_power_save_mode_subtitle);
        a2.setContentText(String.format(string, new Object[]{i + "%"}));
        a2.setShowWhen(false);
        a2.setAutoCancel(true);
        a2.setLargeIcon(l.a(context));
        if (Build.VERSION.SDK_INT < 26) {
            a2.setPriority(2);
            a2.setDefaults(-1);
        }
        Intent intent = new Intent(context, PowerCenter.class);
        intent.setFlags(268435456);
        a2.setContentIntent(PendingIntent.getActivity(context, 0, intent, 0));
        Notification build = a2.build();
        a.a(build, true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        if (Build.VERSION.SDK_INT < 26) {
            build.defaults = -1;
        }
        v.a(notificationManager, "com.miui.securitycenter", context.getResources().getString(R.string.notify_channel_name_security), 5);
        notificationManager.notify(R.string.notification_exit_power_save_mode, build);
    }

    /* access modifiers changed from: private */
    public void c() {
        int i = y.i();
        if (i != 0 && j.b()) {
            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.setAction("com.miui.powercenter.action.DISABLE_MOBILE_DATA");
            PendingIntent b2 = g.b((Context) this, 0, intent);
            if (b2 != null) {
                ((AlarmManager) getSystemService("alarm")).set(2, SystemClock.elapsedRealtime() + ((long) (i * 1000)), b2);
                this.e = true;
            }
        } else if (this.e) {
            a();
            this.e = false;
        }
    }

    /* access modifiers changed from: private */
    public void d() {
        boolean z;
        int l = y.l();
        if (l > 0 && j.b()) {
            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.setAction("com.miui.powercenter.action.CLEAN_MEMORY");
            PendingIntent b2 = g.b((Context) this, B.j(), intent);
            if (b2 != null) {
                ((AlarmManager) getSystemService("alarm")).set(2, SystemClock.elapsedRealtime() + ((long) (l * 1000)), b2);
                z = true;
            } else {
                return;
            }
        } else if (this.f) {
            b();
            z = false;
        } else {
            return;
        }
        this.f = z;
    }

    /* access modifiers changed from: private */
    public void e() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setAction("com.miui.powercenter.action.TRY_ENABLE_MOBILE_DATA");
        sendBroadcastAsUser(intent, B.k());
    }

    private void f() {
        C0530i.a((Context) this).a((C0530i.b) new g(this));
    }

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("=== PowerSaveService info ===");
        a(fileDescriptor, printWriter, strArr);
        this.f7165c.a(fileDescriptor, printWriter, strArr);
        this.f7164b.a(fileDescriptor, printWriter, strArr);
        printWriter.println();
        d.a(printWriter);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        this.f7166d = new h();
        this.f7166d.a((Context) this);
        this.f7163a = new d();
        this.f7163a.a((Context) this);
        this.f7164b = new C0479h(this);
        this.f7164b.a((Context) this);
        this.f7165c = new C0477f(this);
        this.f7165c.a((Context) this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.System.ACTION_SCREEN_ON);
        intentFilter.addAction(Constants.System.ACTION_SCREEN_OFF);
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        registerReceiver(this.h, intentFilter);
        com.miui.powercenter.savemode.a.a((Context) this, 10000);
        com.miui.powercenter.bootshutdown.a.a(this);
        com.miui.powercenter.bootshutdown.a.b(this);
        f();
        if (k.o(this) && (Build.VERSION.SDK_INT < 24 || !isDeviceProtectedStorage())) {
            com.miui.superpower.o.a(getApplicationContext());
        }
        com.miui.powercenter.b.a.a(this);
        if (B.f()) {
            C0516u.a((Context) this);
        }
        Log.i("PowerSaveService", "PowerSaveService created");
    }

    public void onDestroy() {
        super.onDestroy();
        d dVar = this.f7163a;
        if (dVar != null) {
            dVar.b((Context) this);
        }
        this.f7164b.b((Context) this);
        this.f7165c.b((Context) this);
        unregisterReceiver(this.h);
        this.f7166d.f();
        Log.w("PowerSaveService", "PowerSaveService destroyed");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x006a, code lost:
        if (r1 != false) goto L_0x009f;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int onStartCommand(android.content.Intent r9, int r10, int r11) {
        /*
            r8 = this;
            if (r9 != 0) goto L_0x0007
            int r9 = super.onStartCommand(r9, r10, r11)
            return r9
        L_0x0007:
            java.lang.String r0 = r9.getAction()
            java.lang.String r1 = "com.miui.powercenter.action.CHANGE_POWER_MODE_ALARM"
            boolean r0 = r1.equals(r0)
            r1 = 0
            if (r0 == 0) goto L_0x0022
            java.lang.String r0 = "extra_key_power_mode_open"
            boolean r0 = r9.getBooleanExtra(r0, r1)
            com.miui.powercenter.utils.o.a((android.content.Context) r8, (boolean) r0)
            com.miui.powercenter.savemode.a.b(r8)
            goto L_0x011c
        L_0x0022:
            java.lang.String r0 = r9.getAction()
            java.lang.String r2 = "com.miui.powercenter.action.TIME_AUTO_TASK_ALARM"
            boolean r0 = r2.equals(r0)
            java.lang.String r2 = "task_restore"
            r3 = 0
            java.lang.String r5 = "task_id"
            java.lang.String r6 = "PowerSaveService"
            if (r0 == 0) goto L_0x0048
            java.lang.String r0 = "ACTION_TIME_AUTO_TASK_ALARM"
            android.util.Log.d(r6, r0)
            long r3 = r9.getLongExtra(r5, r3)
            boolean r0 = r9.getBooleanExtra(r2, r1)
            com.miui.powercenter.autotask.C0475d.b(r8, r3, r0)
            goto L_0x011c
        L_0x0048:
            java.lang.String r0 = r9.getAction()
            java.lang.String r7 = "com.miui.powercenter.action.APPLY_AUTO_TASK_ALARM"
            boolean r0 = r7.equals(r0)
            if (r0 == 0) goto L_0x006d
            java.lang.String r0 = "ACTION_APPLY_AUTO_TASK_ALARM"
            android.util.Log.d(r6, r0)
            long r3 = r9.getLongExtra(r5, r3)
            boolean r0 = r9.getBooleanExtra(r2, r1)
            java.lang.String r2 = "hide_notification"
            boolean r1 = r9.getBooleanExtra(r2, r1)
            com.miui.powercenter.autotask.C0475d.a(r8, r3, r0)
            if (r1 == 0) goto L_0x011c
            goto L_0x009f
        L_0x006d:
            java.lang.String r0 = r9.getAction()
            java.lang.String r2 = "com.miui.powercenter.action.CANCEL_APPLY_AUTO_TASK_ALARM"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0087
            java.lang.String r0 = "ACTION_CANCEL_APPLY_AUTO_TASK_ALARM"
            android.util.Log.d(r6, r0)
            long r0 = r9.getLongExtra(r5, r3)
            com.miui.powercenter.autotask.C0475d.a(r8, r0)
            goto L_0x011c
        L_0x0087:
            java.lang.String r0 = r9.getAction()
            java.lang.String r2 = "com.miui.powercenter.action.APPLY_AUTO_TASK_NOW"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x00a4
            java.lang.String r0 = "ACTION_APPLY_AUTO_TASK_NOW"
            android.util.Log.d(r6, r0)
            long r2 = r9.getLongExtra(r5, r3)
            com.miui.powercenter.autotask.C0475d.a(r8, r2, r1)
        L_0x009f:
            com.miui.powercenter.autotask.C0495y.a(r8)
            goto L_0x011c
        L_0x00a4:
            java.lang.String r0 = r9.getAction()
            java.lang.String r1 = "com.miui.powercenter.action.CLEAN_CLOUD_WHITE_LIST_UPDATED"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x00b9
            java.lang.String r0 = "ACTION_CLEAN_CLOUD_WHITE_LIST_UPDATED"
            android.util.Log.d(r6, r0)
            r8.f()
            goto L_0x011c
        L_0x00b9:
            java.lang.String r0 = r9.getAction()
            java.lang.String r1 = "com.miui.powercenter.action.TRY_CLOSE_SAVE_MODE"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x00de
            java.lang.String r0 = "ACTION_TRY_CLOSE_SAVE_MODE"
            android.util.Log.d(r6, r0)
            boolean r0 = com.miui.powercenter.y.u()
            if (r0 == 0) goto L_0x00d6
            boolean r0 = com.miui.powercenter.savemode.a.a()
            if (r0 != 0) goto L_0x011c
        L_0x00d6:
            int r0 = com.miui.powercenter.utils.o.e(r8)
            r8.a(r8, r0)
            goto L_0x011c
        L_0x00de:
            java.lang.String r0 = r9.getAction()
            java.lang.String r1 = "com.miui.powercenter.action.ENTERSUPERPOWER_FROMNOTIFICATION"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x00ff
            java.lang.String r0 = "SuperPowerSaveManager"
            java.lang.String r1 = "enter superpower from notification"
            android.util.Log.d(r0, r1)
            java.lang.String r0 = "notification"
            com.miui.superpower.b.h.a((java.lang.String) r0)
            com.miui.superpower.o r0 = com.miui.superpower.o.a((android.content.Context) r8)
            r1 = 1
            r0.a((boolean) r1, (boolean) r1)
            goto L_0x011c
        L_0x00ff:
            java.lang.String r0 = r9.getAction()
            java.lang.String r1 = "com.miui.powercenter.action.BATTERYHISTORY_RECORD"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x011c
            boolean r0 = b.b.c.j.B.f()
            if (r0 == 0) goto L_0x011c
            android.content.Context r0 = r8.getApplicationContext()
            com.miui.powercenter.batteryhistory.u r0 = com.miui.powercenter.batteryhistory.C0516u.a((android.content.Context) r0)
            r0.a()
        L_0x011c:
            int r9 = super.onStartCommand(r9, r10, r11)
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.provider.PowerSaveService.onStartCommand(android.content.Intent, int, int):int");
    }
}
