package com.miui.applicationlock.c;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.j.B;
import b.b.c.j.d;
import b.b.o.g.e;
import com.miui.applicationlock.C0312y;
import com.miui.applicationlock.TransitionHelper;
import com.miui.applicationlock.a.h;
import com.miui.appmanager.C0322e;
import com.miui.maml.folme.AnimatedTarget;
import com.miui.networkassistant.config.Constants;
import com.miui.securitycenter.R;
import com.miui.securitycenter.service.RemoteService;
import miui.os.UserHandle;
import miui.process.IForegroundInfoListener;
import miui.security.SecurityManager;

/* renamed from: com.miui.applicationlock.c.k  reason: case insensitive filesystem */
public class C0267k {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public Context f3310a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public C0259c f3311b;

    /* renamed from: c  reason: collision with root package name */
    private IForegroundInfoListener.Stub f3312c = new C0265i(this);

    public C0267k(Context context) {
        this.f3310a = context;
        this.f3311b = C0259c.b(this.f3310a);
        new C0260d(this).execute(new Void[0]);
        c();
    }

    private void a() {
        ((AlarmManager) this.f3310a.getSystemService("alarm")).cancel(PendingIntent.getService(this.f3310a, 0, new Intent(this.f3310a, RemoteService.class), 0));
    }

    public static void a(Context context, Intent intent) {
        o.e(g(context));
    }

    public static void a(String str, Context context, boolean z) {
        if (B.j() == 0) {
            new C0261e(context, str, z).execute(new Void[0]);
        }
    }

    /* access modifiers changed from: private */
    public static void b(Context context, int i, String str) {
        Intent intent = new Intent(context, RemoteService.class);
        intent.putExtra("cmd", str);
        ((AlarmManager) context.getSystemService("alarm")).cancel(PendingIntent.getService(context, i, intent, 0));
    }

    /* access modifiers changed from: private */
    public static void b(Context context, long j, String str, int i) {
        Intent intent = new Intent(context, RemoteService.class);
        intent.putExtra("cmd", str);
        ((AlarmManager) context.getSystemService("alarm")).set(2, SystemClock.elapsedRealtime() + j, PendingIntent.getService(context, i, intent, 0));
    }

    public static void b(Context context, String str) {
        new Handler(Looper.getMainLooper()).postDelayed(new C0266j(context, str), 1000);
    }

    public static void b(String str, Context context, boolean z) {
        if (B.c() == UserHandle.myUserId() && B.j() == 0) {
            String a2 = C0322e.a(context, str);
            if (!TextUtils.isEmpty(a2) && !Constants.System.ANDROID_PACKAGE_NAME.equals(a2) && !z && C0312y.f3468b.contains(str) && g(context)) {
                o.d(str);
                o.e(true);
                h.k();
            }
        }
    }

    private static boolean b() {
        return System.currentTimeMillis() - o.g() > 604800000;
    }

    /* access modifiers changed from: private */
    public static boolean b(C0259c cVar) {
        return o.d() < 1 && !cVar.d() && cVar.j() && !o.t();
    }

    /* access modifiers changed from: private */
    public static Intent c(Context context, String str) {
        Intent intent = new Intent(context, TransitionHelper.class);
        intent.putExtra(AnimatedTarget.STATE_TAG_FROM, "AlarmReceiver");
        intent.putExtra("enter_way", str);
        return intent;
    }

    private void c() {
        try {
            e.a(Class.forName("miui.process.ProcessManager"), "registerForegroundInfoListener", (Class<?>[]) new Class[]{IForegroundInfoListener.class}, this.f3312c);
        } catch (Exception e) {
            Log.e("AppLockServices", e.toString());
        }
    }

    public static void c(Context context) {
        if (B.j() == 0) {
            new C0264h(context).execute(new Void[0]);
        }
    }

    public static void d(Context context) {
        d.a(new C0262f(context));
    }

    public static void e(Context context) {
        d.a(new C0263g(context));
    }

    public static void f(Context context) {
        if (B.j() == 0) {
            if (!b(C0259c.b(context)) && o.p()) {
                if (o.l() < 1) {
                    b(context, o.a(1, 21, 30), "app_installed_scan", 3);
                } else {
                    b(context, 3, "app_installed_scan");
                }
            }
            o.a(false);
        }
    }

    /* access modifiers changed from: private */
    public static boolean g(Context context) {
        int k = o.k();
        return u.a(context) && !C0259c.b(context).d() && k >= 0 && k < 3 && b();
    }

    /* access modifiers changed from: private */
    public static boolean h(Context context) {
        C0259c b2 = C0259c.b(context);
        return !b2.d() || (b2.d() && o.a((SecurityManager) context.getSystemService("security")).size() == 0);
    }

    public void a(Intent intent, Context context) {
        if ("handle_notifycation".equals(intent.getStringExtra("param"))) {
            int d2 = o.d();
            if (b(this.f3311b)) {
                o.a(context, R.string.ac_notification_contentTitle, R.string.ac_notification_contentText, c(context, "00002"), 102, 1, BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_card_app_lock));
                h.h("guide_notification");
                o.b(d2 + 1);
                return;
            }
            a();
        }
    }
}
