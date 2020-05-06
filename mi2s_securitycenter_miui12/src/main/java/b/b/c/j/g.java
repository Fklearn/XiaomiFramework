package b.b.c.j;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.util.Log;
import b.b.o.g.c;

public class g {
    public static int a(Context context) {
        try {
            return ((Integer) c.a(ContextWrapper.class, (Object) context, "getUserId", (Class<?>[]) null, new Object[0])).intValue();
        } catch (Exception e) {
            Log.e("ContextCompat", "getUserId exception: ", e);
            return 0;
        }
    }

    public static PendingIntent a(Context context, int i, Intent intent) {
        return a(context, i, intent, 134217728, (Bundle) null, B.b());
    }

    public static PendingIntent a(Context context, int i, Intent intent, int i2, Bundle bundle, UserHandle userHandle) {
        try {
            return (PendingIntent) c.a((Class<?>) PendingIntent.class, "getActivityAsUser", (Class<?>[]) new Class[]{Context.class, Integer.TYPE, Intent.class, Integer.TYPE, Bundle.class, UserHandle.class}, context, Integer.valueOf(i), intent, Integer.valueOf(i2), bundle, userHandle);
        } catch (Exception e) {
            Log.i("ContextCompat", "getActivityAsUser exception!!!", e);
            return null;
        }
    }

    public static PendingIntent a(Context context, int i, Intent intent, int i2, UserHandle userHandle) {
        try {
            return (PendingIntent) c.a((Class<?>) PendingIntent.class, "getBroadcastAsUser", (Class<?>[]) new Class[]{Context.class, Integer.TYPE, Intent.class, Integer.TYPE, UserHandle.class}, context, Integer.valueOf(i), intent, Integer.valueOf(i2), userHandle);
        } catch (Exception e) {
            Log.i("ContextCompat", "getBroadcastAsUser exception!!!", e);
            return null;
        }
    }

    public static Context a(Context context, int i, UserHandle userHandle) {
        try {
            return (Context) c.a(ContextWrapper.class, (Object) context, "createPackageContextAsUser", (Class<?>[]) new Class[]{String.class, Integer.TYPE, UserHandle.class}, context.getPackageName(), Integer.valueOf(i), userHandle);
        } catch (Exception e) {
            Log.i("ContextCompat", "createPackageContextAsUser exception!!!", e);
            return context;
        }
    }

    public static void a(NotificationManager notificationManager, int i) {
        a(notificationManager, (String) null, i, B.b());
    }

    public static void a(NotificationManager notificationManager, int i, Notification notification) {
        a(notificationManager, (String) null, i, notification, B.b());
    }

    public static void a(NotificationManager notificationManager, String str, int i, Notification notification, UserHandle userHandle) {
        try {
            c.a((Object) notificationManager, "notifyAsUser", (Class<?>[]) new Class[]{String.class, Integer.TYPE, Notification.class, UserHandle.class}, str, Integer.valueOf(i), notification, userHandle);
        } catch (Exception e) {
            Log.i("ContextCompat", "notifyAsUser exception!!!", e);
        }
    }

    public static void a(NotificationManager notificationManager, String str, int i, UserHandle userHandle) {
        try {
            c.a((Object) notificationManager, "cancelAsUser", (Class<?>[]) new Class[]{String.class, Integer.TYPE, UserHandle.class}, str, Integer.valueOf(i), userHandle);
        } catch (Exception e) {
            Log.i("ContextCompat", "cancelAsUser exception!!!", e);
        }
    }

    public static void a(Context context, BroadcastReceiver broadcastReceiver, UserHandle userHandle, IntentFilter intentFilter) {
        a(context, broadcastReceiver, userHandle, intentFilter, "android.permission.INTERACT_ACROSS_USERS_FULL", (Handler) null);
    }

    public static void a(Context context, BroadcastReceiver broadcastReceiver, UserHandle userHandle, IntentFilter intentFilter, String str, Handler handler) {
        try {
            c.a(ContextWrapper.class, (Object) context, "registerReceiverAsUser", (Class<?>[]) new Class[]{BroadcastReceiver.class, UserHandle.class, IntentFilter.class, String.class, Handler.class}, broadcastReceiver, userHandle, intentFilter, str, handler);
        } catch (Exception e) {
            Log.i("ContextCompat", "registerReceiverAsUser exception!!!", e);
        }
    }

    public static void a(Context context, Intent intent, UserHandle userHandle) {
        try {
            c.a(ContextWrapper.class, (Object) context, "sendBroadcastAsUser", (Class<?>[]) new Class[]{Intent.class, UserHandle.class}, intent, userHandle);
        } catch (Exception e) {
            Log.i("ContextCompat", "sendBroadcastAsUser exception!!!", e);
        }
    }

    public static void a(Context context, Intent intent, UserHandle userHandle, String str) {
        try {
            c.a(ContextWrapper.class, (Object) context, "sendBroadcastAsUser", (Class<?>[]) new Class[]{Intent.class, UserHandle.class, String.class}, intent, userHandle, str);
        } catch (Exception e) {
            Log.i("ContextCompat", "sendBroadcastAsUser exception!!!", e);
        }
    }

    public static boolean a(Context context, Intent intent, ServiceConnection serviceConnection, int i, UserHandle userHandle) {
        try {
            return ((Boolean) c.a(ContextWrapper.class, (Object) context, "bindServiceAsUser", (Class<?>[]) new Class[]{Intent.class, ServiceConnection.class, Integer.TYPE, UserHandle.class}, intent, serviceConnection, Integer.valueOf(i), userHandle)).booleanValue();
        } catch (Exception e) {
            Log.i("ContextCompat", "bindServiceAsUser exception!!!", e);
            return false;
        }
    }

    public static PendingIntent b(Context context, int i, Intent intent) {
        return a(context, i, intent, 134217728, B.b());
    }

    public static void b(Context context, Intent intent, UserHandle userHandle) {
        try {
            c.a(ContextWrapper.class, (Object) context, "startActivityAsUser", (Class<?>[]) new Class[]{Intent.class, UserHandle.class}, intent, userHandle);
        } catch (Exception e) {
            Log.i("ContextCompat", "startActivityAsUser exception!!!", e);
        }
    }

    public static void c(Context context, Intent intent, UserHandle userHandle) {
        Class<ContextWrapper> cls = ContextWrapper.class;
        try {
            c.a((Class) cls, (Object) context.getApplicationContext(), "startServiceAsUser", (Class<?>[]) new Class[]{Intent.class, UserHandle.class}, intent, userHandle);
        } catch (Exception e) {
            Log.i("ContextCompat", "startServiceAsUser exception!!!", e);
        }
    }
}
