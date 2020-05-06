package com.miui.gamebooster.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import com.milink.api.v1.type.MilinkConfig;
import com.miui.gamebooster.gbservices.H;
import com.miui.gamebooster.m.C0381l;
import com.miui.gamebooster.m.C0393y;
import com.miui.gamebooster.mutiwindow.l;
import com.miui.gamebooster.service.IVideoToolBox;
import com.miui.gamebooster.videobox.settings.f;
import com.miui.gamebooster.videobox.utils.e;
import com.miui.networkassistant.config.Constants;
import java.util.ArrayList;
import java.util.List;

public class VideoToolBoxService extends Service {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static ArrayList<String> f4798a = new ArrayList<>();
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public Handler f4799b;

    /* renamed from: c  reason: collision with root package name */
    private Handler f4800c;

    /* renamed from: d  reason: collision with root package name */
    private HandlerThread f4801d;
    /* access modifiers changed from: private */
    public Context e;
    /* access modifiers changed from: private */
    public boolean f;
    private volatile boolean g;
    /* access modifiers changed from: private */
    public final Object h = new Object();
    /* access modifiers changed from: private */
    public ArrayList<String> i = new ArrayList<>();
    private VideoToolBoxBinder j;
    /* access modifiers changed from: private */
    public String k;
    private ContentObserver l = new I(this, this.f4799b);
    private BroadcastReceiver m = new J(this);
    private l.a n = new K(this);
    private BroadcastReceiver o = new L(this);

    public class VideoToolBoxBinder extends IVideoToolBox.Stub {
        public VideoToolBoxBinder() {
        }

        public void c(List<String> list) {
            if (list != null) {
                VideoToolBoxService.this.i.clear();
                VideoToolBoxService.this.i.addAll(list);
            }
        }
    }

    public class a implements Runnable {
        public a() {
        }

        public void run() {
            if (f.b(VideoToolBoxService.this.e) && f.h() && VideoToolBoxService.this.i.isEmpty()) {
                f.a(false);
                List<PackageInfo> a2 = b.b.c.b.b.a(VideoToolBoxService.this.e).a();
                ArrayList<String> a3 = com.miui.common.persistence.b.a("gb_added_games", (ArrayList<String>) new ArrayList());
                List<String> a4 = f.a(VideoToolBoxService.this.e);
                for (PackageInfo next : a2) {
                    if (next != null && !a3.contains(next.packageName) && a4.contains(next.packageName)) {
                        VideoToolBoxService.this.i.add(next.packageName);
                    }
                }
                Log.i("VideoToolBoxService", "set default vtb apps = " + VideoToolBoxService.this.i);
                f.b((ArrayList<String>) VideoToolBoxService.this.i);
            }
        }
    }

    public class b extends Handler {
        public b(Looper looper) {
            super(looper);
        }

        private void a(H h) {
            if (!TextUtils.isEmpty(VideoToolBoxService.this.k) && !VideoToolBoxService.this.i.isEmpty() && VideoToolBoxService.this.i.contains(VideoToolBoxService.this.k)) {
                h.b();
            }
        }

        public void handleMessage(Message message) {
            H a2 = H.a(VideoToolBoxService.this.e, VideoToolBoxService.this.f4799b);
            switch (message.what) {
                case 1:
                    a2.b();
                    return;
                case 2:
                case 3:
                case 6:
                    a2.c();
                    return;
                case 4:
                    VideoToolBoxService.this.i();
                    return;
                case 5:
                    VideoToolBoxService.this.f();
                    return;
                case 7:
                    a(a2);
                    return;
                default:
                    return;
            }
        }
    }

    static {
        f4798a.add("com.miui.screenrecorder");
        f4798a.add("com.lbe.security.miui");
        f4798a.add(MilinkConfig.PACKAGE_NAME);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0009, code lost:
        r4 = ((android.app.ActivityManager) getSystemService("activity")).getRunningTasks(1);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean a(java.lang.String r4) {
        /*
            r3 = this;
            java.lang.String r0 = "com.miui.securitycenter"
            boolean r4 = r0.equals(r4)
            r0 = 0
            if (r4 == 0) goto L_0x0033
            java.lang.String r4 = "activity"
            java.lang.Object r4 = r3.getSystemService(r4)
            android.app.ActivityManager r4 = (android.app.ActivityManager) r4
            r1 = 1
            java.util.List r4 = r4.getRunningTasks(r1)
            if (r4 == 0) goto L_0x0033
            int r2 = r4.size()
            if (r2 <= 0) goto L_0x0033
            java.lang.Object r4 = r4.get(r0)
            android.app.ActivityManager$RunningTaskInfo r4 = (android.app.ActivityManager.RunningTaskInfo) r4
            android.content.ComponentName r4 = r4.topActivity
            java.lang.String r4 = r4.getClassName()
            java.lang.String r2 = "com.miui.gamebooster.ui.GameBoxAlertActivity"
            boolean r4 = r2.equals(r4)
            if (r4 == 0) goto L_0x0033
            return r1
        L_0x0033:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.service.VideoToolBoxService.a(java.lang.String):boolean");
    }

    private void b() {
        this.f4800c = new Handler(Looper.getMainLooper());
        this.f4801d = new HandlerThread("video_toolbox_service");
        this.f4801d.start();
        this.f4799b = new b(this.f4801d.getLooper());
    }

    private void c() {
        l.a().a(this.n);
        this.g = true;
    }

    private void d() {
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("gb.action.update_video_list");
            LocalBroadcastManager.getInstance(this.e).registerReceiver(this.m, intentFilter);
        } catch (Exception unused) {
        }
    }

    private void e() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.System.ACTION_SCREEN_OFF);
        intentFilter.addAction(Constants.System.ACTION_USER_PRESENT);
        intentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        registerReceiver(this.o, intentFilter);
    }

    /* access modifiers changed from: private */
    public void f() {
        if (f.k()) {
            String a2 = com.miui.common.persistence.b.a("key_hang_up_pkg", (String) null);
            if (a2 != null) {
                C0393y.a((Context) this, a2, false);
                com.miui.common.persistence.b.b("key_hang_up_pkg", (String) null);
            }
            f.d(false);
        }
    }

    private void g() {
        this.g = false;
        l.a().b(this.n);
    }

    private void h() {
        try {
            LocalBroadcastManager.getInstance(this.e).unregisterReceiver(this.m);
        } catch (Exception unused) {
        }
    }

    /* access modifiers changed from: private */
    public void i() {
        this.i.clear();
        this.i.addAll(f.a((ArrayList<String>) new ArrayList()));
    }

    public IBinder onBind(Intent intent) {
        return this.j;
    }

    public void onCreate() {
        super.onCreate();
        Log.i("VideoToolBoxService", "onCreate");
        if (C0381l.c(getApplicationContext())) {
            Log.d("VideoToolBoxService", "do not launch video toolbox service in kid space");
            return;
        }
        this.e = this;
        this.j = new VideoToolBoxBinder();
        b();
        this.f = e.a() && f.b(this.e);
        this.i = f.a(this.i);
        if (this.f && f.h() && this.i.isEmpty()) {
            this.f4799b.post(new a());
        }
        c();
        this.e.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("pref_videobox_switch_status"), true, this.l);
        e();
        d();
    }

    public void onDestroy() {
        super.onDestroy();
        g();
        try {
            this.e.getContentResolver().unregisterContentObserver(this.l);
            unregisterReceiver(this.o);
            h();
        } catch (Exception unused) {
        }
        this.f4799b.sendEmptyMessage(2);
    }
}
