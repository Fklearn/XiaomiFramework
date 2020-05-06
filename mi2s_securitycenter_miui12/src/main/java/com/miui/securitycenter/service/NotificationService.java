package com.miui.securitycenter.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;
import b.b.a.e.c;
import b.b.c.j.e;
import b.b.c.j.n;
import b.b.c.j.s;
import b.b.c.j.v;
import com.miui.networkassistant.config.Constants;
import com.miui.securitycenter.R;
import com.miui.securitycenter.h;
import java.lang.ref.WeakReference;
import miui.provider.ExtraSettings;

public class NotificationService extends Service {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static final long f7518a = (e.c() / 1024);

    /* renamed from: b  reason: collision with root package name */
    private Handler f7519b = new Handler();
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public int f7520c = 0;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public boolean f7521d = false;
    /* access modifiers changed from: private */
    public boolean e = true;
    private NotificationManager f;
    /* access modifiers changed from: private */
    public long g = 0;
    /* access modifiers changed from: private */
    public boolean h = true;
    /* access modifiers changed from: private */
    public HandlerThread i;
    /* access modifiers changed from: private */
    public a j;
    /* access modifiers changed from: private */
    public long k = -1;
    private ContentObserver l = new e(this, this.f7519b);
    private BroadcastReceiver m = new f(this);
    private final BroadcastReceiver n = new g(this);
    private final BroadcastReceiver o = new h(this);

    public static class NotificationView extends RemoteViews {
        private Context context;
        private String pkgName;

        public NotificationView(Context context2, String str, int i) {
            super(str, i);
            this.pkgName = str;
            this.context = context2;
        }

        /* access modifiers changed from: private */
        public void refreshGarbageCleanView() {
            String string = this.context.getString(R.string.menu_text_garbage_cleanup);
            boolean h = h.h(this.context);
            long b2 = h.b(this.context);
            boolean z = true;
            if (!h || b2 <= 0) {
                z = false;
            } else {
                Context context2 = this.context;
                string = context2.getString(R.string.menu_text_garbage_cleanup_danger, new Object[]{n.d(context2, b2, 0)});
            }
            setImageViewResource(R.id.iv_antispam, R.drawable.icon_garbage_clean_notification_normal);
            setTextViewText(R.id.iv_antispam_text, string);
            int color = this.context.getResources().getColor(R.color.notification_title_text_red_color);
            int color2 = this.context.getResources().getColor(R.color.notification_title_text_color);
            if (!z) {
                color = color2;
            }
            setTextColor(R.id.iv_antispam_text, color);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: android.text.SpannableString} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: java.lang.String} */
        /* access modifiers changed from: private */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void refreshMemoryRemoteView() {
            /*
                r10 = this;
                long r0 = b.b.c.j.e.a()
                r2 = 1024(0x400, double:5.06E-321)
                long r0 = r0 / r2
                long unused = com.miui.securitycenter.service.NotificationService.f7518a
                float r2 = (float) r0
                r3 = 1065353216(0x3f800000, float:1.0)
                float r2 = r2 * r3
                long r4 = com.miui.securitycenter.service.NotificationService.f7518a
                float r4 = (float) r4
                float r4 = r4 * r3
                float r2 = r2 / r4
                r3 = 1036831949(0x3dcccccd, float:0.1)
                int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                r3 = 1
                r4 = 0
                if (r2 >= 0) goto L_0x0020
                r2 = r3
                goto L_0x0021
            L_0x0020:
                r2 = r4
            L_0x0021:
                java.lang.String r0 = b.b.c.j.n.a(r0)
                android.content.Context r1 = r10.context
                r5 = 2131756836(0x7f100724, float:1.914459E38)
                java.lang.Object[] r6 = new java.lang.Object[r3]
                r6[r4] = r0
                java.lang.String r1 = r1.getString(r5, r6)
                android.text.SpannableString r5 = new android.text.SpannableString
                r5.<init>(r1)
                android.text.style.ForegroundColorSpan r6 = new android.text.style.ForegroundColorSpan
                android.content.Context r7 = r10.context
                android.content.res.Resources r7 = r7.getResources()
                r8 = 2131100324(0x7f0602a4, float:1.7813026E38)
                int r7 = r7.getColor(r8)
                r6.<init>(r7)
                int r7 = r0.length()
                r8 = 33
                r5.setSpan(r6, r4, r7, r8)
                android.text.style.ForegroundColorSpan r4 = new android.text.style.ForegroundColorSpan
                android.content.Context r6 = r10.context
                android.content.res.Resources r6 = r6.getResources()
                r7 = 2131100323(0x7f0602a3, float:1.7813024E38)
                int r6 = r6.getColor(r7)
                r4.<init>(r6)
                int r6 = r0.length()
                int r7 = r1.length()
                int r9 = r0.length()
                if (r7 <= r9) goto L_0x0078
                int r0 = r1.length()
                int r0 = r0 - r3
                goto L_0x007c
            L_0x0078:
                int r0 = r0.length()
            L_0x007c:
                r5.setSpan(r4, r6, r0, r8)
                r0 = 2131297120(0x7f090360, float:1.8212176E38)
                if (r2 == 0) goto L_0x0085
                r1 = r5
            L_0x0085:
                r10.setTextViewText(r0, r1)
                r0 = 2131297119(0x7f09035f, float:1.8212174E38)
                r1 = 2131231794(0x7f080432, float:1.807968E38)
                r10.setImageViewResource(r0, r1)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.securitycenter.service.NotificationService.NotificationView.refreshMemoryRemoteView():void");
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v1, resolved type: android.text.SpannableString} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: java.lang.String} */
        /* access modifiers changed from: private */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void refreshPowerRemoteView(int r9) {
            /*
                r8 = this;
                r0 = 2131297128(0x7f090368, float:1.8212192E38)
                r1 = 2131231816(0x7f080448, float:1.8079724E38)
                r8.setImageViewResource(r0, r1)
                android.content.Context r0 = r8.context
                java.lang.String r0 = com.miui.powercenter.utils.u.a(r0, r9)
                android.content.Context r1 = r8.context
                r2 = 1
                java.lang.Object[] r3 = new java.lang.Object[r2]
                r4 = 0
                r3[r4] = r0
                r4 = 2131756880(0x7f100750, float:1.914468E38)
                java.lang.String r1 = r1.getString(r4, r3)
                android.text.SpannableString r3 = new android.text.SpannableString
                r3.<init>(r1)
                int r4 = r1.indexOf(r0)
                int r0 = r0.length()
                int r0 = r0 + r4
                android.text.style.ForegroundColorSpan r5 = new android.text.style.ForegroundColorSpan
                android.content.Context r6 = r8.context
                android.content.res.Resources r6 = r6.getResources()
                r7 = 2131100324(0x7f0602a4, float:1.7813026E38)
                int r6 = r6.getColor(r7)
                r5.<init>(r6)
                r6 = 33
                r3.setSpan(r5, r4, r0, r6)
                android.text.style.ForegroundColorSpan r4 = new android.text.style.ForegroundColorSpan
                android.content.Context r5 = r8.context
                android.content.res.Resources r5 = r5.getResources()
                r7 = 2131100323(0x7f0602a3, float:1.7813024E38)
                int r5 = r5.getColor(r7)
                r4.<init>(r5)
                int r5 = r1.length()
                if (r5 <= r0) goto L_0x0062
                int r5 = r1.length()
                int r2 = r5 + -1
                goto L_0x0063
            L_0x0062:
                r2 = r0
            L_0x0063:
                r3.setSpan(r4, r0, r2, r6)
                r0 = 2131297129(0x7f090369, float:1.8212194E38)
                r2 = 20
                if (r9 >= r2) goto L_0x006e
                r1 = r3
            L_0x006e:
                r8.setTextViewText(r0, r1)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.securitycenter.service.NotificationService.NotificationView.refreshPowerRemoteView(int):void");
        }

        public void init() {
            setOnClickPendingIntent(R.id.ll_frame, (PendingIntent) null);
            try {
                PackageManager packageManager = this.context.getPackageManager();
                setImageViewBitmap(16908294, ((BitmapDrawable) packageManager.getApplicationInfo(this.pkgName, 0).loadIcon(packageManager)).getBitmap());
            } catch (Exception e) {
                Log.e("NotificationService", "setImageViewBitmap exception", e);
            }
            Intent intent = new Intent("com.miui.securitycenter.action.TRACK_NOTIFICATION_CLICK");
            setOnClickPendingIntent(16908294, PendingIntent.getBroadcast(this.context, 0, new Intent(intent).putExtra("track_module", "securitycenter"), 1073741824));
            setOnClickPendingIntent(R.id.ll_barbage, PendingIntent.getBroadcast(this.context, 1, new Intent(intent).putExtra("track_module", "memory_clean"), 1073741824));
            setOnClickPendingIntent(R.id.ll_antispam, PendingIntent.getBroadcast(this.context, 2, new Intent(intent).putExtra("track_module", "garbage_clean"), 1073741824));
            setOnClickPendingIntent(R.id.ll_power, PendingIntent.getBroadcast(this.context, 3, new Intent(intent).putExtra("track_module", "powercenter"), 1073741824));
        }
    }

    private static class a extends Handler {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<NotificationService> f7522a;

        public a(NotificationService notificationService, Looper looper) {
            super(looper);
            this.f7522a = new WeakReference<>(notificationService);
        }

        /* access modifiers changed from: private */
        public void a() {
            s.a("startCycle");
            NotificationService notificationService = (NotificationService) this.f7522a.get();
            if (notificationService != null) {
                post(new j(this, notificationService));
            }
        }

        /* access modifiers changed from: private */
        public void b() {
            s.a("stopCycle");
            removeCallbacksAndMessages((Object) null);
            getLooper().quit();
        }
    }

    /* access modifiers changed from: private */
    public void a(long j2) {
        s.a("screen on : " + this.e + ", enable : " + this.f7521d);
        if (this.e && this.f7521d) {
            this.f7519b.postDelayed(new i(this), j2);
        } else if (!this.h) {
            this.j.b();
            b();
            this.h = true;
        }
    }

    private void b() {
        s.a("cancelNotification");
        this.f.cancel(20004);
    }

    /* access modifiers changed from: private */
    public static void b(Context context) {
        Intent intent = new Intent("miui.intent.action.ANTISPAM_UPDATE");
        intent.putExtra("has_intercept", c.c(context) ? 1 : 0);
        context.sendStickyBroadcast(intent);
    }

    /* access modifiers changed from: private */
    public void c() {
        s.a("notifyNotification");
        NotificationView notificationView = new NotificationView(this, getPackageName(), R.layout.m_notification_remoteview);
        notificationView.init();
        notificationView.refreshMemoryRemoteView();
        notificationView.refreshPowerRemoteView(this.f7520c);
        notificationView.refreshGarbageCleanView();
        Notification build = v.a((Context) this, "securitycenter_resident_notification").setOngoing(true).setPriority(2).setSmallIcon(R.drawable.security_small_icon).setGroup("ResidentGroup").setWhen(System.currentTimeMillis()).setContent(notificationView).build();
        v.c(build, false);
        v.b(build, false);
        v.a(this.f, "securitycenter_resident_notification", getResources().getString(R.string.notify_channel_optimize), 5);
        this.f.notify(20004, build);
        this.k = e.a();
        this.g = System.currentTimeMillis();
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        s.a("onConfig");
        c();
    }

    public void onCreate() {
        super.onCreate();
        s.a("NotificationService onCreate");
        this.f = (NotificationManager) getSystemService("notification");
        Notification build = v.a((Context) this, "securitycenter_resident_notification").build();
        v.a(this.f, "securitycenter_resident_notification", getResources().getString(R.string.notify_channel_optimize), 5);
        startForeground(20005, build);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.System.ACTION_SCREEN_OFF);
        intentFilter.addAction(Constants.System.ACTION_SCREEN_ON);
        registerReceiver(this.n, intentFilter);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("com.miui.securitycenter.action.UPDATE_NOTIFICATION");
        intentFilter2.addAction("com.miui.securitycenter.action.CLEAR_MEMORY");
        intentFilter2.addAction("android.intent.action.BATTERY_CHANGED");
        registerReceiver(this.m, intentFilter2);
        IntentFilter intentFilter3 = new IntentFilter();
        intentFilter3.addAction("com.miui.securitycenter.action.TRACK_NOTIFICATION_CLICK");
        registerReceiver(this.o, intentFilter3);
        getContentResolver().registerContentObserver(ExtraSettings.Secure.getUriFor("has_new_antispam"), false, this.l);
    }

    public void onDestroy() {
        this.f7521d = false;
        unregisterReceiver(this.m);
        unregisterReceiver(this.n);
        unregisterReceiver(this.o);
        getContentResolver().unregisterContentObserver(this.l);
        this.f7519b.removeCallbacksAndMessages((Object) null);
        a aVar = this.j;
        if (aVar != null) {
            aVar.b();
        }
        b();
        super.onDestroy();
    }

    public int onStartCommand(Intent intent, int i2, int i3) {
        if (intent != null) {
            this.f7521d = intent.getBooleanExtra("notify", true);
        }
        return 1;
    }
}
