package com.miui.cleanmaster;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.service.notification.StatusBarNotification;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.j.e;
import b.b.c.j.g;
import b.b.c.j.n;
import b.b.c.j.v;
import b.b.o.a.a;
import com.google.android.exoplayer2.C;
import com.miui.common.persistence.b;
import com.miui.securitycenter.R;
import com.miui.securitycenter.h;
import com.miui.securityscan.a.G;
import com.miui.securityscan.i.q;

public class m {

    /* renamed from: a  reason: collision with root package name */
    private static m f3758a;

    /* renamed from: b  reason: collision with root package name */
    private n f3759b;

    private m(Context context) {
        this.f3759b = new n(context);
    }

    public static synchronized m a(Context context) {
        m mVar;
        synchronized (m.class) {
            if (f3758a == null) {
                f3758a = new m(context.getApplicationContext());
            }
            mVar = f3758a;
        }
        return mVar;
    }

    public static void a(Context context, String str, String str2, @DrawableRes int i, PendingIntent pendingIntent, int i2, String str3) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        v.a(notificationManager, str3, context.getString(R.string.activity_title_garbage_cleanup), 4);
        Notification.Builder a2 = v.a(context, str3);
        a2.setDefaults(32).setWhen(System.currentTimeMillis()).setPriority(2).setAutoCancel(true).setOngoing(false).setSmallIcon(R.drawable.cleanmaster_small_icon).setLargeIcon(BitmapFactory.decodeResource(context.getResources(), i)).setContentIntent(pendingIntent).setContentTitle(str).setContentText(str2);
        if (Build.VERSION.SDK_INT < 26) {
            a2.setPriority(1).setDefaults(1);
        }
        Notification build = a2.build();
        a.b(build, true);
        a.a(build, true);
        a.a(build, 0);
        g.a(notificationManager, i2, build);
    }

    private static void a(Context context, String str, String str2, @DrawableRes int i, PendingIntent pendingIntent, int i2, boolean z, boolean z2) {
        Notification.Builder a2 = v.a(context, "com.miui.cleanmaster");
        a2.setSmallIcon(R.drawable.cleanmaster_small_icon);
        a2.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), i));
        a2.setContentTitle(str);
        a2.setContentText(str2);
        a2.setWhen(System.currentTimeMillis());
        a2.setContentIntent(pendingIntent);
        a2.setAutoCancel(true);
        Notification build = a2.build();
        build.tickerText = str + ":" + str2;
        build.flags = build.flags | 16;
        if (z) {
            build.flags |= 32;
        }
        if (Build.VERSION.SDK_INT > 23) {
            a2.setGroupSummary(false).setGroup("cleanMasterGroup");
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        if (z2) {
            v.a(notificationManager, "com.miui.cleanmaster", context.getResources().getString(R.string.activity_title_garbage_cleanup), 4);
            if (Build.VERSION.SDK_INT < 26) {
                a2.setPriority(1).setDefaults(1);
            }
        } else {
            int i3 = 3;
            if (e.b() <= 8) {
                i3 = 2;
            }
            v.a(notificationManager, "com.miui.cleanmaster", context.getResources().getString(R.string.activity_title_garbage_cleanup), i3);
        }
        a.a(build, true);
        a.a(build, 0);
        notificationManager.notify(i2, build);
    }

    public static void a(Context context, boolean z) {
        boolean g = h.g(context);
        long a2 = h.a(context);
        Log.i("NotificationHelper", "onReceive: \t generalNeed = " + g + "\t generalSize = " + a2);
        if (g && a2 > 0) {
            boolean a3 = a(context, "general_last_time", "general_show_cnt", "cm_general_clean_notification_cnt", 5);
            boolean a4 = a(context, a(context).a("key_time_garbage_cleanup"));
            Log.i("NotificationHelper", "checkAndSendGeneralNotification: alreadyHas " + a4);
            if (!z || (z && a4 && a3)) {
                d(context, z);
            }
        }
    }

    public static boolean a(Context context, int i) {
        StatusBarNotification[] activeNotifications;
        if (Build.VERSION.SDK_INT >= 23 && (activeNotifications = ((NotificationManager) context.getSystemService("notification")).getActiveNotifications()) != null) {
            for (StatusBarNotification id : activeNotifications) {
                if (id.getId() == i) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean a(Context context, String str, String str2, String str3, int i) {
        d a2 = d.a(context);
        long currentTimeMillis = System.currentTimeMillis();
        long a3 = a2.a(str);
        Log.i("NotificationHelper", "isFrequencyValid: \t lastTimeKey = " + str + "\t lastTime = " + a3 + "\t current = " + currentTimeMillis);
        if (currentTimeMillis - a3 > 86400000) {
            a2.a(str2, 0);
            return true;
        }
        int b2 = a2.b(str2);
        int a4 = b.a(str3, i);
        Log.i("NotificationHelper", "isFrequencyValid: \t showCntKey = " + str2 + "\t cloudCnt = " + a4 + "\t showdCnt = " + b2);
        return b2 <= a4;
    }

    public static void b(Context context) {
        long c2 = h.c(context);
        Resources resources = context.getResources();
        Context context2 = context;
        a(context2, resources.getString(R.string.appcleaner_wechat_notification_garbage_cleanup_title), resources.getString(R.string.appcleaner_wechat_notification_garbage_cleanup_summary, new Object[]{n.d(context, c2, 2)}), R.drawable.wechat_notify_icon, PendingIntent.getActivity(context, 0, new Intent("miui.intent.action.GARBAGE_DEEPCLEAN_WECHAT"), 1073741824), a(context).a("key_wechat_time_garbage_cleanup"), false, false);
        d a2 = d.a(context);
        a2.a("wechat_show_cnt", a2.b("wechat_show_cnt") + 1);
        a2.a("wechat_last_time", System.currentTimeMillis());
        G.l();
    }

    public static void b(Context context, boolean z) {
        boolean l = h.l(context);
        long c2 = h.c(context);
        Log.i("NotificationHelper", "onReceive: \t wechatNeed = " + l + "\t wechatSize = " + c2);
        if (l && !miui.os.Build.IS_INTERNATIONAL_BUILD && c2 > 0) {
            boolean a2 = a(context, "wechat_last_time", "wechat_show_cnt", "cm_wechat_notification_cnt", 5);
            boolean a3 = a(context, a(context).a("key_wechat_time_garbage_cleanup"));
            if (!z || (z && a3 && a2)) {
                b(context);
            }
        }
    }

    public static void c(Context context) {
        long d2 = h.d(context);
        Resources resources = context.getResources();
        Context context2 = context;
        a(context2, resources.getString(R.string.global_wa_setting_alert_dialog_title), resources.getString(R.string.global_wa_setting_alert_dialog_content, new Object[]{n.d(context, d2, 2)}), R.drawable.icon_whatsapp_cleaner, PendingIntent.getActivity(context, 0, new Intent("miui.intent.action.GARBAGE_DEEPCLEAN_WHATSAPP"), 1073741824), a(context).a("key_whatsapp_time_garbage_cleanup"), false, false);
        d a2 = d.a(context);
        a2.a("whatsapp_show_cnt", a2.b("whatsapp_show_cnt") + 1);
        a2.a("whatsapp_last_time", System.currentTimeMillis());
        G.m();
    }

    public static void c(Context context, boolean z) {
        boolean m = h.m(context);
        long d2 = h.d(context);
        Log.i("NotificationHelper", "onReceive: \t whatsappNeed = " + m + "\t whatsAppSize = " + d2);
        if (m && miui.os.Build.IS_INTERNATIONAL_BUILD && d2 > 0) {
            boolean a2 = a(context, "whatsapp_last_time", "whatsapp_show_cnt", "cm_whatsapp_clean_notification_cnt", 5);
            boolean a3 = a(context, a(context).a("key_whatsapp_time_garbage_cleanup"));
            if (!z || (z && a3 && a2)) {
                c(context);
            }
        }
    }

    public static void d(Context context, boolean z) {
        String str;
        long a2 = h.a(context);
        Resources resources = context.getResources();
        String string = resources.getString(R.string.notification_garbage_cleanup_title);
        boolean z2 = false;
        String string2 = resources.getString(R.string.notification_garbage_cleanup_summary, new Object[]{n.d(context, a2, 2)});
        Intent intent = new Intent("miui.intent.action.GARBAGE_CLEANUP");
        intent.putExtra("extra_auto_start_scan", true);
        intent.putExtra("enter_homepage_way", "00005");
        PendingIntent activity = PendingIntent.getActivity(context, 0, intent, 1073741824);
        int a3 = a(context).a("key_time_garbage_cleanup");
        d a4 = d.a(context);
        if (!z) {
            a4.c(a4.c() + 1);
        }
        if (a4.c() > 2) {
            z2 = true;
        }
        Log.i("NotificationHelper", "showGeneralNotificaiton: invalid cnt = " + a4.c());
        if (!z2) {
            Log.i("NotificationHelper", "showGeneralNotificaiton: Normal");
            a(context, string, string2, R.drawable.ic_launcher_rubbish_clean, activity, a3, false, false);
            str = "show_clean_alert";
        } else if (!d(context)) {
            Log.i("NotificationHelper", "showGeneralNotificaiton: OnGoing");
            a(context, string, string2, R.drawable.ic_launcher_rubbish_clean, activity, a3, true, false);
            str = "show_clean_ongoing_alert";
        } else {
            Log.i("NotificationHelper", "showGeneralNotificaiton: Floating");
            if (!z) {
                a(context, string, string2, R.drawable.ic_launcher_rubbish_clean, activity, a3, "com.miui.cleanmaster.high");
            } else {
                a(context, string, string2, R.drawable.ic_launcher_rubbish_clean, activity, a3, false, false);
            }
            str = "show_clean_floating_alert";
        }
        a4.a("general_show_cnt", a4.b("general_show_cnt") + 1);
        a4.a("general_last_time", System.currentTimeMillis());
        G.n(str);
    }

    private static boolean d(Context context) {
        d a2 = d.a(context);
        return q.a(Environment.getDataDirectory().getPath()).f7740a > ((long) (miui.os.Build.IS_INTERNATIONAL_BUILD ? a2.b() : a2.a())) * C.NANOS_PER_SECOND;
    }

    public int a(String str) {
        if (TextUtils.equals(str, "key_perm_notification_bar")) {
            return 1000;
        }
        return this.f3759b.a(str);
    }
}
