package b.b.c.j;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ParceledListSlice;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import b.b.o.g.c;
import b.b.o.g.e;
import com.market.sdk.utils.d;
import com.miui.gamebooster.m.C0384o;
import com.miui.networkassistant.utils.PackageUtil;
import java.util.Arrays;
import java.util.List;
import miui.util.Log;

public class v {

    /* renamed from: a  reason: collision with root package name */
    private static String f1766a = "NotificationUtils";

    private static int a(int i) {
        if (Build.VERSION.SDK_INT < 26) {
            return 3;
        }
        if (i == 0) {
            return ((Integer) c.a((Class<?>) NotificationManager.class, "IMPORTANCE_NONE")).intValue();
        }
        if (i == 1) {
            return ((Integer) c.a((Class<?>) NotificationManager.class, "IMPORTANCE_MIN")).intValue();
        }
        if (i == 2) {
            return ((Integer) c.a((Class<?>) NotificationManager.class, "IMPORTANCE_LOW")).intValue();
        }
        if (i == 4) {
            return ((Integer) c.a((Class<?>) NotificationManager.class, "IMPORTANCE_HIGH")).intValue();
        }
        if (i == 5) {
            return ((Integer) c.a((Class<?>) NotificationManager.class, "IMPORTANCE_MAX")).intValue();
        }
        try {
            return ((Integer) c.a((Class<?>) NotificationManager.class, "IMPORTANCE_DEFAULT")).intValue();
        } catch (Exception e) {
            Log.e(f1766a, "getImportance exception: ", e);
            return 3;
        }
    }

    public static Notification.Builder a(Context context, String str) {
        Notification.Builder builder = new Notification.Builder(context);
        if (Build.VERSION.SDK_INT < 26) {
            return builder;
        }
        try {
            c.a a2 = c.a.a("android.app.Notification$Builder");
            a2.a(new Class[]{Context.class, String.class}, context, str);
            return (Notification.Builder) a2.b();
        } catch (Exception e) {
            Log.e(f1766a, "createNotificationBuilder exception: ", e);
            return builder;
        }
    }

    public static String a(Notification notification) {
        try {
            return (String) c.a((Object) notification, String.class, "getChannelId", (Class<?>[]) null, new Object[0]);
        } catch (Exception e) {
            Log.e(f1766a, "getChannelId exception: ", e);
            return "com.miui.securitycenter";
        }
    }

    public static void a(Notification.Builder builder) {
        String str;
        Bundle bundle = new Bundle();
        try {
            str = (String) e.a(d.a("android.app.MiuiNotification"), "EXTRA_SHOW_ACTION", String.class);
        } catch (Exception e) {
            String str2 = f1766a;
            android.util.Log.e(str2, "getFieldValue EXTRA_SHOW_ACTION failed. " + e.toString());
            str = "miui.showAction";
        }
        bundle.putBoolean(str, true);
        builder.setExtras(bundle);
    }

    public static void a(Notification.Builder builder, Notification.Action action) {
        if (Build.VERSION.SDK_INT >= 20) {
            builder.addAction(action);
        }
    }

    public static void a(Notification notification, int i) {
        try {
            c.a a2 = c.a.a((Object) notification);
            a2.b("extraNotification");
            a2.e();
            a2.a("setMessageCount", new Class[]{Integer.TYPE}, Integer.valueOf(i));
        } catch (Exception e) {
            Log.e(f1766a, "setMessageCount exception: ", e);
        }
    }

    public static void a(Notification notification, boolean z) {
        try {
            c.a a2 = c.a.a((Object) notification);
            a2.b("extraNotification");
            a2.e();
            a2.a("setCustomizedIcon", new Class[]{Boolean.TYPE}, Boolean.valueOf(z));
        } catch (Exception e) {
            Log.e(f1766a, "setCustomizedIcon exception: ", e);
        }
    }

    public static void a(NotificationManager notificationManager, String str) {
        try {
            c.a((Object) notificationManager, "deleteNotificationChannel", (Class<?>[]) new Class[]{String.class}, str);
        } catch (Exception e) {
            Log.e(f1766a, "deleteNotificationChannel exception: ", e);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v6, resolved type: java.lang.Object[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void a(android.app.NotificationManager r7, java.lang.String r8, java.lang.String r9, int r10) {
        /*
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 26
            if (r0 >= r1) goto L_0x0007
            return
        L_0x0007:
            int r10 = a((int) r10)
            java.lang.String r0 = "android.app.NotificationChannel"
            b.b.o.g.c$a r0 = b.b.o.g.c.a.a((java.lang.String) r0)     // Catch:{ Exception -> 0x0075 }
            r1 = 3
            java.lang.Class[] r2 = new java.lang.Class[r1]     // Catch:{ Exception -> 0x0075 }
            java.lang.Class<java.lang.String> r3 = java.lang.String.class
            r4 = 0
            r2[r4] = r3     // Catch:{ Exception -> 0x0075 }
            java.lang.Class<java.lang.CharSequence> r3 = java.lang.CharSequence.class
            r5 = 1
            r2[r5] = r3     // Catch:{ Exception -> 0x0075 }
            java.lang.Class r3 = java.lang.Integer.TYPE     // Catch:{ Exception -> 0x0075 }
            r6 = 2
            r2[r6] = r3     // Catch:{ Exception -> 0x0075 }
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ Exception -> 0x0075 }
            r1[r4] = r8     // Catch:{ Exception -> 0x0075 }
            r1[r5] = r9     // Catch:{ Exception -> 0x0075 }
            java.lang.Integer r8 = java.lang.Integer.valueOf(r10)     // Catch:{ Exception -> 0x0075 }
            r1[r6] = r8     // Catch:{ Exception -> 0x0075 }
            r0.a(r2, r1)     // Catch:{ Exception -> 0x0075 }
            java.lang.String r8 = "setSound"
            java.lang.Class[] r9 = new java.lang.Class[r6]     // Catch:{ Exception -> 0x0075 }
            java.lang.Class<android.net.Uri> r10 = android.net.Uri.class
            r9[r4] = r10     // Catch:{ Exception -> 0x0075 }
            java.lang.Class<android.media.AudioAttributes> r10 = android.media.AudioAttributes.class
            r9[r5] = r10     // Catch:{ Exception -> 0x0075 }
            java.lang.Object[] r10 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x0075 }
            r1 = 0
            r10[r4] = r1     // Catch:{ Exception -> 0x0075 }
            r10[r5] = r1     // Catch:{ Exception -> 0x0075 }
            r0.a(r8, r9, r10)     // Catch:{ Exception -> 0x0075 }
            java.lang.String r8 = "setVibrationPattern"
            java.lang.Class[] r9 = new java.lang.Class[r5]     // Catch:{ Exception -> 0x0075 }
            java.lang.Class<long[]> r10 = long[].class
            r9[r4] = r10     // Catch:{ Exception -> 0x0075 }
            java.lang.Object[] r10 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x0075 }
            long[] r1 = new long[r5]     // Catch:{ Exception -> 0x0075 }
            r2 = 0
            r1[r4] = r2     // Catch:{ Exception -> 0x0075 }
            r10[r4] = r1     // Catch:{ Exception -> 0x0075 }
            r0.a(r8, r9, r10)     // Catch:{ Exception -> 0x0075 }
            java.lang.Object r8 = r0.b()     // Catch:{ Exception -> 0x0075 }
            java.lang.String r9 = "createNotificationChannel"
            java.lang.Class[] r10 = new java.lang.Class[r5]     // Catch:{ Exception -> 0x0075 }
            java.lang.String r0 = "android.app.NotificationChannel"
            java.lang.Class r0 = java.lang.Class.forName(r0)     // Catch:{ Exception -> 0x0075 }
            r10[r4] = r0     // Catch:{ Exception -> 0x0075 }
            java.lang.Object[] r0 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x0075 }
            r0[r4] = r8     // Catch:{ Exception -> 0x0075 }
            b.b.o.g.c.a((java.lang.Object) r7, (java.lang.String) r9, (java.lang.Class<?>[]) r10, (java.lang.Object[]) r0)     // Catch:{ Exception -> 0x0075 }
            goto L_0x007d
        L_0x0075:
            r7 = move-exception
            java.lang.String r8 = f1766a
            java.lang.String r9 = "createNotificationChannel exception: "
            miui.util.Log.e(r8, r9, r7)
        L_0x007d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.c.j.v.a(android.app.NotificationManager, java.lang.String, java.lang.String, int):void");
    }

    public static void a(Context context, String str, String str2, int i) {
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                int a2 = a(i);
                List asList = Arrays.asList(new Object[]{c.a(Class.forName("android.app.NotificationChannel"), (Class<?>[]) new Class[]{String.class, CharSequence.class, Integer.TYPE}, str, str2, Integer.valueOf(a2))});
                int uidByPackageName = PackageUtil.getUidByPackageName(context, context.getPackageName());
                if (uidByPackageName != -1) {
                    Object a3 = c.a(Class.forName("android.app.NotificationManager"), "getService", (Class<?>[]) null, new Object[0]);
                    c.a(a3, "createNotificationChannelsForPackage", (Class<?>[]) new Class[]{String.class, Integer.TYPE, ParceledListSlice.class}, context.getPackageName(), Integer.valueOf(uidByPackageName), new ParceledListSlice(asList));
                    if (B.a(context)) {
                        int a4 = B.a(C0384o.a(context.getContentResolver(), (String) c.a(Class.forName("android.provider.MiuiSettings$Secure"), "SECOND_USER_ID", String.class), (int) UserHandle.USER_NULL, 0), uidByPackageName);
                        c.a(a3, "createNotificationChannelsForPackage", (Class<?>[]) new Class[]{String.class, Integer.TYPE, ParceledListSlice.class}, context.getPackageName(), Integer.valueOf(a4), new ParceledListSlice(asList));
                    }
                }
            } catch (Exception e) {
                Log.e(f1766a, "createNotificationChannelForAllUsers exception: ", e);
            }
        }
    }

    public static void b(Notification notification, boolean z) {
        try {
            c.a a2 = c.a.a((Object) notification);
            a2.b("extraNotification");
            a2.e();
            a2.a("setEnableFloat", new Class[]{Boolean.TYPE}, Boolean.valueOf(z));
        } catch (Exception e) {
            Log.e(f1766a, "setEnableFloat exception: ", e);
        }
    }

    public static void c(Notification notification, boolean z) {
        try {
            c.a a2 = c.a.a((Object) notification);
            a2.b("extraNotification");
            a2.e();
            a2.a("setEnableKeyguard", new Class[]{Boolean.TYPE}, Boolean.valueOf(z));
        } catch (Exception e) {
            Log.e(f1766a, "setEnableKeyguard exception: ", e);
        }
    }
}
