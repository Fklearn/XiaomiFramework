package com.miui.gamebooster.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import b.b.c.j.s;
import b.b.c.j.x;
import com.milink.api.v1.type.MilinkConfig;
import com.miui.applicationlock.c.K;
import com.miui.common.persistence.b;
import com.miui.gamebooster.c.a;
import com.miui.gamebooster.m.C0371b;
import com.miui.gamebooster.m.C0381l;
import com.miui.gamebooster.m.C0382m;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.m.S;
import com.miui.gamebooster.m.U;
import com.miui.gamebooster.mutiwindow.l;
import com.miui.gamebooster.service.IGameBooster;
import com.miui.luckymoney.config.AppConstants;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.utils.DateUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.security.SecurityManager;

public class GameBoosterService extends Service {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static ArrayList<String> f4758a = new ArrayList<>();
    /* access modifiers changed from: private */
    public boolean A = false;
    /* access modifiers changed from: private */
    public ServiceConnection B = new C0400a(this);
    private BroadcastReceiver C = new C0401b(this);
    private l.a D = new C0403d(this);
    private BroadcastReceiver E = new C0406g(this);
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public long f4759b = 0;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public long f4760c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public long f4761d;
    private long e;
    /* access modifiers changed from: private */
    public int f = b.a("gb_notification_business_period", 0);
    private int g = b.a("xunyou_alert_dialog_first_count", 0);
    private int h = b.a("xunyou_alert_dialog_overdue_gift_count", 0);
    /* access modifiers changed from: private */
    public Handler i;
    private Handler j;
    private HandlerThread k;
    private GameBoosterBinder l;
    private SecurityManager m;
    /* access modifiers changed from: private */
    public IGameBoosterWindow n;
    private Intent o;
    /* access modifiers changed from: private */
    public Context p;
    private ArrayList<String> q = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<String> r;
    /* access modifiers changed from: private */
    public Boolean s = false;
    private long t = 0;
    /* access modifiers changed from: private */
    public Boolean u = false;
    /* access modifiers changed from: private */
    public Boolean v;
    /* access modifiers changed from: private */
    public boolean w;
    /* access modifiers changed from: private */
    public boolean x;
    /* access modifiers changed from: private */
    public boolean y;
    /* access modifiers changed from: private */
    public Object z = new Object();

    public class GameBoosterBinder extends IGameBooster.Stub {
        public GameBoosterBinder() {
        }

        /* access modifiers changed from: private */
        public void a(String str) {
            Log.i("GameBoosterService", "packageName: " + str);
            GameBoosterService.this.g();
            Log.i("GameBoosterService", "gamebooster:" + SystemClock.elapsedRealtime());
        }

        public void A() {
            GameBoosterService gameBoosterService = GameBoosterService.this;
            Boolean unused = gameBoosterService.v = Boolean.valueOf(a.a((Context) gameBoosterService).k(true));
        }

        public String F() {
            return U.b(GameBoosterService.this.p);
        }

        public void K() {
            long unused = GameBoosterService.this.f4760c = b.a("gamebooster_xunyou_cache_time", -1);
            int unused2 = GameBoosterService.this.f = b.a("gb_notification_business_period", 0);
        }

        public void L() {
            GameBoosterService gameBoosterService = GameBoosterService.this;
            U.a((Context) gameBoosterService, r.a((Context) gameBoosterService, gameBoosterService.i).a());
        }

        public void b(int i) {
            GameBoosterService.this.i.post(new C0410k(this, i));
        }

        public void b(List<String> list) {
            synchronized (GameBoosterService.this.z) {
                if (com.miui.securityscan.c.a.f7625a) {
                    Log.i("GameBoosterService", "setAddedGames" + list.toString());
                }
                GameBoosterService.this.r.clear();
                GameBoosterService.this.r.addAll(list);
                int size = GameBoosterService.this.r.size();
                if (size > 0) {
                    r.a((Context) GameBoosterService.this, GameBoosterService.this.i).a((String[]) GameBoosterService.this.r.toArray(new String[size]));
                }
            }
        }

        public void f(String str) {
            if (!GameBoosterService.this.u.booleanValue() || GameBoosterService.this.s.booleanValue()) {
                if (!GameBoosterService.this.u.booleanValue()) {
                    Boolean unused = GameBoosterService.this.u = true;
                }
                a(str);
                return;
            }
            GameBoosterService.this.a((Boolean) true);
            GameBoosterService.this.i.postDelayed(new C0409j(this, str), 200);
        }

        public void n() {
            GameBoosterService.this.i.sendEmptyMessage(119);
            boolean unused = GameBoosterService.this.w = false;
        }
    }

    static {
        f4758a.add("com.miui.screenrecorder");
        f4758a.add("com.lbe.security.miui");
        f4758a.add(MilinkConfig.PACKAGE_NAME);
    }

    private void a(Context context) {
        List<ApplicationInfo> installedApplications = context.getPackageManager().getInstalledApplications(0);
        ArrayList<String> a2 = C0382m.a("gamebooster_gamelist", context.getApplicationContext());
        ArrayList<String> c2 = C0382m.c("gamebooster", "gblist", context);
        if (c2.size() <= 100) {
            c2 = a2;
        }
        for (ApplicationInfo next : installedApplications) {
            if (x.a(next) && c2.contains(next.packageName)) {
                this.q.add(next.packageName);
            }
        }
    }

    /* access modifiers changed from: private */
    public void a(Context context, boolean z2) {
        new C0407h(this, context, z2).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0041  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0058  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void a(android.content.Intent r4, java.lang.String r5, long r6, int r8) {
        /*
            r3 = this;
            r3.e = r6
            long r6 = r3.e
            java.lang.String r0 = "gamebooster_xunyou_alert_last_time"
            com.miui.common.persistence.b.b((java.lang.String) r0, (long) r6)
            int r6 = r5.hashCode()
            r7 = -1679992814(0xffffffff9bdd5812, float:-3.6618297E-22)
            r0 = 2
            r1 = 0
            r2 = 1
            if (r6 == r7) goto L_0x0034
            r7 = -892847763(0xffffffffcac8396d, float:-6560950.5)
            if (r6 == r7) goto L_0x002a
            r7 = 506548898(0x1e3152a2, float:9.38739E-21)
            if (r6 == r7) goto L_0x0020
            goto L_0x003e
        L_0x0020:
            java.lang.String r6 = "xunyou_alert_dialog_expired"
            boolean r6 = r5.equals(r6)
            if (r6 == 0) goto L_0x003e
            r6 = r1
            goto L_0x003f
        L_0x002a:
            java.lang.String r6 = "xunyou_alert_dialog_first"
            boolean r6 = r5.equals(r6)
            if (r6 == 0) goto L_0x003e
            r6 = r2
            goto L_0x003f
        L_0x0034:
            java.lang.String r6 = "xunyou_alert_dialog_overdue_gift"
            boolean r6 = r5.equals(r6)
            if (r6 == 0) goto L_0x003e
            r6 = r0
            goto L_0x003f
        L_0x003e:
            r6 = -1
        L_0x003f:
            if (r6 == 0) goto L_0x0058
            if (r6 == r2) goto L_0x004f
            if (r6 == r0) goto L_0x0046
            goto L_0x005d
        L_0x0046:
            java.lang.String r6 = "gt_xunyou_net_booster_try_again_dialog_show_again"
            boolean r6 = com.miui.common.persistence.b.a((java.lang.String) r6, (boolean) r1)
            if (r6 == 0) goto L_0x005d
            return
        L_0x004f:
            java.lang.String r6 = "gamebooster_free_send_netbooster_open_nomore"
            boolean r6 = com.miui.common.persistence.b.a((java.lang.String) r6, (boolean) r1)
            if (r6 == 0) goto L_0x005d
            return
        L_0x0058:
            java.lang.String r6 = "expired"
            r4.putExtra(r6, r8)
        L_0x005d:
            java.lang.String r6 = "alertType"
            r4.putExtra(r6, r5)
            r5 = 0
            com.miui.gamebooster.m.C0393y.a((android.content.Context) r3, (android.content.Intent) r4, (java.lang.String) r5, (boolean) r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.service.GameBoosterService.a(android.content.Intent, java.lang.String, long, int):void");
    }

    /* access modifiers changed from: private */
    public void a(Boolean bool) {
        if (bool.booleanValue()) {
            this.i.sendEmptyMessage(117);
        } else {
            this.i.sendEmptyMessageDelayed(117, 1500);
        }
    }

    private void a(String str, int i2) {
        b.b(str, i2);
    }

    public static void a(String str, Context context) {
        ArrayList<String> a2 = b.a("already_added_game", (ArrayList<String>) new ArrayList());
        Iterator<String> it = a2.iterator();
        while (it.hasNext()) {
            String next = it.next();
            if (!x.h(context, next)) {
                a2.remove(next);
            } else if (a2.contains(str)) {
                return;
            }
        }
        a2.add(str);
        b.b("already_added_game", a2);
        S.b(context, a2.size());
    }

    /* access modifiers changed from: private */
    public void a(boolean z2, Context context) {
        this.i.post(new C0405f(this, z2, context));
    }

    /* access modifiers changed from: private */
    public boolean a(String str) {
        List<ActivityManager.RunningTaskInfo> runningTasks;
        if (("com.miui.securitycenter".equals(str) || "com.miui.home".equals(str)) && this.A) {
            return true;
        }
        boolean isGameBoosterActived = this.m.isGameBoosterActived(UserHandle.getCallingUserId());
        if (("com.miui.securitycenter".equals(str) || AppConstants.Package.PACKAGE_NAME_QQ.equals(str)) && (runningTasks = ((ActivityManager) getSystemService("activity")).getRunningTasks(1)) != null && runningTasks.size() > 0 && ((("com.miui.gamebooster.ui.WindowCallActivity".equals(runningTasks.get(0).topActivity.getClassName()) || "com.tencent.av.ui.VChatActivity".equals(runningTasks.get(0).topActivity.getClassName())) && isGameBoosterActived) || "com.miui.gamebooster.ui.GameBoxAlertActivity".equals(runningTasks.get(0).topActivity.getClassName()))) {
            return true;
        }
        if (!b.a("gb_show_window", false)) {
            return false;
        }
        Log.i("GameBoosterService", "filter:GAMEBOOSTER_SHOWWINDOW true");
        return true;
    }

    private void c() {
        this.j = new Handler(getMainLooper());
        this.k = new HandlerThread("gamebooster_bg_service");
        this.k.start();
        this.i = new C0404e(this, this.k.getLooper());
    }

    /* access modifiers changed from: private */
    public void d() {
        this.i.sendEmptyMessage(118);
    }

    private void e() {
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("gb.action.update_game_list");
            LocalBroadcastManager.getInstance(this).registerReceiver(this.C, intentFilter);
        } catch (Exception e2) {
            Log.e("GameBoosterService", "initLocalBroadcastReceiver: failed=" + e2.toString());
        }
    }

    /* access modifiers changed from: private */
    public boolean f() {
        int fromNowDayInterval = DateUtil.getFromNowDayInterval(this.f4760c);
        if (fromNowDayInterval >= -1 && com.miui.gamebooster.d.a.a() && K.c(this)) {
            long currentTimeMillis = System.currentTimeMillis();
            int fromNowDayInterval2 = DateUtil.getFromNowDayInterval(this.e);
            Intent intent = new Intent("com.miui.gamebooster.action.XUNYOU_ALERT_ACTIVITY");
            long j2 = this.f4760c;
            if ((j2 == -1 || j2 == 0) && this.y && fromNowDayInterval2 > 30 && this.g < 3) {
                a(intent, "xunyou_alert_dialog_first", currentTimeMillis, 0);
                this.g++;
                a("xunyou_alert_dialog_first_count", this.g);
                return true;
            }
            long j3 = this.f4760c;
            if (j3 <= 0 || j3 >= currentTimeMillis) {
                if (fromNowDayInterval == -1 && fromNowDayInterval2 > 1) {
                    a(intent, "xunyou_alert_dialog_expired", currentTimeMillis, 1);
                    return true;
                }
            } else if (fromNowDayInterval <= 0 || fromNowDayInterval >= 4 || fromNowDayInterval2 <= 3) {
                int i2 = this.f;
                if (i2 > 0 && fromNowDayInterval > i2 && fromNowDayInterval2 > 30 && this.h < 3) {
                    a(intent, "xunyou_alert_dialog_overdue_gift", currentTimeMillis, 0);
                    this.h++;
                    a("xunyou_alert_dialog_overdue_gift_count", this.h);
                    return true;
                }
            } else {
                a(intent, "xunyou_alert_dialog_overdue", currentTimeMillis, 0);
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void g() {
        this.i.sendEmptyMessage(115);
    }

    /* access modifiers changed from: private */
    public void h() {
        this.i.sendEmptyMessage(116);
    }

    /* access modifiers changed from: private */
    public void i() {
        try {
            l.a().a(this.D);
            Log.i("GameBoosterService", "registerWhetStoneSuccess");
            this.s = true;
            r a2 = r.a((Context) this, this.i);
            int size = this.r.size();
            if (size > 0) {
                a2.a((String[]) this.r.toArray(new String[size]));
            }
            if (this.q.size() == 0) {
                a((Context) this);
            }
        } catch (Exception e2) {
            Log.e("GameBoosterService", e2.toString());
        }
    }

    private void j() {
        k();
        if (a.v(false) && a.y(false) && b.a("key_gamebooster_support_sign_function", false)) {
            C0371b.b(this);
        }
    }

    private void k() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.System.ACTION_SCREEN_OFF);
        intentFilter.addAction(Constants.System.ACTION_USER_PRESENT);
        intentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        intentFilter.addAction("com.miui.gamebooster.action.SIGN_NOTIFICATION");
        intentFilter.addAction("com.miui.gamebooster.service.action.SWITCHANTIMSG");
        intentFilter.addAction("com.miui.gamebooster.action.START_GAMEMODE");
        intentFilter.addAction("com.miui.gamebooster.action.STOP_GAMEMODE");
        intentFilter.addAction("com.miui.gamebooster.action.RESET_USERSTATUS");
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        registerReceiver(this.E, intentFilter);
        b.b("already_added_game", (ArrayList<String>) new ArrayList());
        if (a.a(this.p).k(true)) {
            a((Boolean) false);
        }
    }

    private void l() {
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(this.C);
        } catch (Exception e2) {
            Log.e("GameBoosterService", "releaseLocalBroadcastReceiver: failed=" + e2.toString());
        }
    }

    /* access modifiers changed from: private */
    public void m() {
        Log.i("GameBoosterService", "start:startServerControl");
        r.a((Context) this, this.i).l();
        if (!this.s.booleanValue()) {
            r.a((Context) this, this.i).a(SystemClock.elapsedRealtime());
            a((Boolean) true);
        }
        if (this.t == 0 || SystemClock.elapsedRealtime() - this.t > 10800000) {
            r.a((Context) this, this.i).j();
            this.t = SystemClock.elapsedRealtime();
        }
    }

    /* access modifiers changed from: private */
    public void n() {
        Log.i("GameBoosterService", "open_windows");
        this.o = new Intent(this, GameBoxWindowManagerService.class);
        this.o.setAction("com.miui.gamebooster.service.GameBoxService");
        this.o.putExtra("intent_gamebooster_window_type", 2);
        this.A = this.p.bindService(this.o, this.B, 1);
        this.x = false;
        a.a((Context) this);
        a.P(false);
    }

    private void o() {
        this.s = false;
        l.a().b(this.D);
    }

    public void a(boolean z2) {
        this.m.setGameBoosterIBinder(this.l, UserHandle.getCallingUserId(), z2);
    }

    public Handler b() {
        return this.j;
    }

    public IBinder onBind(Intent intent) {
        Log.i("GameBoosterService", "return onBinder");
        return this.l;
    }

    public void onCreate() {
        super.onCreate();
        if (C0381l.c(getApplicationContext())) {
            s.a("do not launch gamebooster service in kid space");
            return;
        }
        this.p = this;
        Log.i("GameBoosterService", "OnCREATE");
        this.l = new GameBoosterBinder();
        this.m = (SecurityManager) getSystemService("security");
        c();
        this.i.sendEmptyMessage(125);
        j();
        this.v = Boolean.valueOf(a.a((Context) this).k(true));
        a.a((Context) this);
        this.w = a.e();
        a.a((Context) this);
        this.x = a.d();
        this.f4760c = b.a("gamebooster_xunyou_cache_time", -1);
        this.e = b.a("gamebooster_xunyou_alert_last_time", 0);
        this.y = b.a("gamebooster_xunyou_first_user", false);
        this.r = new ArrayList<>(b.a("gb_added_games", (ArrayList<String>) new ArrayList()));
        if (C0388t.p() && !C0388t.A()) {
            Settings.Secure.putString(this.p.getContentResolver(), "gamebox_stick", (String) null);
        }
        e();
    }

    public void onDestroy() {
        super.onDestroy();
        h();
        o();
        unregisterReceiver(this.E);
        r.a((Context) this, this.i).r();
        l();
        Log.i("GameBoosterService", "on Destory...");
    }
}
