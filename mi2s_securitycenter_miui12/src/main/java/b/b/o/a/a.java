package b.b.o.a;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import b.b.o.g.c;
import b.b.o.g.e;

public class a {
    public static void a(Notification notification, int i) {
        try {
            Object obj = notification.getClass().getDeclaredField("extraNotification").get(notification);
            obj.getClass().getDeclaredMethod("setMessageCount", new Class[]{Integer.TYPE}).invoke(obj, new Object[]{Integer.valueOf(i)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void a(Notification notification, long j) {
        try {
            Object obj = notification.getClass().getDeclaredField("extraNotification").get(notification);
            obj.getClass().getDeclaredMethod("setFloatTime", new Class[]{Long.TYPE}).invoke(obj, new Object[]{Long.valueOf(j)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void a(Notification notification, PendingIntent pendingIntent) {
        try {
            Object obj = notification.getClass().getDeclaredField("extraNotification").get(notification);
            obj.getClass().getDeclaredMethod("setExitFloatingIntent", new Class[]{PendingIntent.class}).invoke(obj, new Object[]{pendingIntent});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void a(Notification notification, boolean z) {
        try {
            e.a(e.a((Object) notification, "extraNotification"), "customizedIcon", (Object) Boolean.valueOf(z));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void a(Context context, String str, boolean z) {
        try {
            c.a.a("miui.util.NotificationFilterHelper").b("enableStatusIcon", new Class[]{Context.class, String.class, Boolean.TYPE}, context, str, Boolean.valueOf(z));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void b(Notification notification, boolean z) {
        try {
            Object obj = notification.getClass().getDeclaredField("extraNotification").get(notification);
            obj.getClass().getDeclaredMethod("setEnableFloat", new Class[]{Boolean.TYPE}).invoke(obj, new Object[]{Boolean.valueOf(z)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
