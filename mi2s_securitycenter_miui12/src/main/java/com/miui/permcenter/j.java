package com.miui.permcenter;

import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.IBinder;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import b.b.c.j.B;
import b.b.c.j.g;
import b.b.o.g.e;
import com.miui.gamebooster.service.ISecurityCenterNotificationListener;
import com.miui.gamebooster.service.NotificationListener;
import com.miui.gamebooster.service.NotificationListenerCallback;
import com.miui.permcenter.privacymanager.b.c;
import com.miui.permcenter.privacymanager.behaviorrecord.o;
import com.miui.permcenter.privacymanager.f;
import java.lang.reflect.InvocationTargetException;

public class j {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static final String f6168a = "j";

    /* renamed from: b  reason: collision with root package name */
    private static volatile j f6169b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public Context f6170c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public ISecurityCenterNotificationListener f6171d;
    /* access modifiers changed from: private */
    public Handler e;
    /* access modifiers changed from: private */
    public f f;
    /* access modifiers changed from: private */
    public NotificationListenerCallback g = new h(this);
    private ServiceConnection h = new i(this);

    private j(Context context) {
        this.f6170c = context.getApplicationContext();
        this.e = new Handler(context.getMainLooper());
        Context context2 = this.f6170c;
        g.a(context2, new Intent(context2, NotificationListener.class), this.h, 1, B.k());
        if (o.a(context)) {
            this.f = f.a(context);
        }
        if (c.a(context)) {
            com.miui.permcenter.privacymanager.b.o.a(context).a();
        }
    }

    private static int a(AppOpsManager appOpsManager, int i, int i2, String str) {
        Class cls = Integer.TYPE;
        try {
            return ((Integer) e.a((Object) appOpsManager, cls, "noteOpNoThrow", (Class<?>[]) new Class[]{cls, cls, String.class}, Integer.valueOf(i), Integer.valueOf(i2), str)).intValue();
        } catch (NoSuchMethodException e2) {
            e2.printStackTrace();
            return 0;
        } catch (IllegalAccessException e3) {
            e3.printStackTrace();
            return 0;
        } catch (InvocationTargetException e4) {
            e4.printStackTrace();
            return 0;
        }
    }

    public static void a(Context context) {
        b(context);
    }

    /* access modifiers changed from: private */
    public void a(Context context, String str, String str2, int i, int i2) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        try {
            IBinder iBinder = (IBinder) e.a(Class.forName("android.os.ServiceManager"), "getService", (Class<?>[]) new Class[]{String.class}, "notification");
            e.b(e.a(Class.forName("android.app.INotificationManager$Stub"), "asInterface", (Class<?>[]) new Class[]{IBinder.class}, iBinder), "cancelNotificationWithTag", new Class[]{String.class, String.class, Integer.TYPE, Integer.TYPE}, str, str2, Integer.valueOf(i), Integer.valueOf(i2));
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public boolean a(String str) {
        Context context = this.f6170c;
        if (context == null) {
            return false;
        }
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(str, 0);
            return applicationInfo != null && (applicationInfo.uid < 10000 || (applicationInfo.flags & 1) != 0);
        } catch (Exception unused) {
        }
    }

    /* access modifiers changed from: private */
    public static int b(StatusBarNotification statusBarNotification) {
        try {
            return ((Integer) e.a((Object) statusBarNotification, Integer.TYPE, "getUid", (Class<?>[]) new Class[0], new Object[0])).intValue();
        } catch (NoSuchMethodException e2) {
            e2.printStackTrace();
            return 0;
        } catch (IllegalAccessException e3) {
            e3.printStackTrace();
            return 0;
        } catch (InvocationTargetException e4) {
            e4.printStackTrace();
            return 0;
        }
    }

    private static void b(Context context) {
        if (f6169b == null) {
            synchronized (j.class) {
                if (f6169b == null) {
                    f6169b = new j(context);
                }
            }
        }
    }

    public boolean a(int i, String str) {
        int a2 = a((AppOpsManager) this.f6170c.getSystemService(AppOpsManager.class), 10026, i, str);
        String str2 = f6168a;
        Log.v(str2, "ret=" + a2);
        return a2 == 0;
    }
}
