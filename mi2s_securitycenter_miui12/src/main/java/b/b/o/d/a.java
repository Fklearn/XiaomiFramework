package b.b.o.d;

import android.content.Context;
import android.os.IBinder;
import android.util.Log;
import b.b.c.j.B;
import b.b.o.g.e;

public class a {
    public static void a(Context context) {
        if (b.a(context) && b.c(context) && b.b(context)) {
            a(context, 2);
        }
    }

    private static void a(Context context, int i) {
        try {
            IBinder iBinder = (IBinder) e.a(Class.forName("android.os.ServiceManager"), "getService", (Class<?>[]) new Class[]{String.class}, "notification");
            IBinder iBinder2 = (IBinder) e.a(e.a(Class.forName("android.app.INotificationManager$Stub"), "asInterface", (Class<?>[]) new Class[]{IBinder.class}, iBinder), IBinder.class, "getColorLightManager", (Class<?>[]) null, new Object[0]);
            Object a2 = e.a(Class.forName("miui.lights.ILightsManager$Stub"), "asInterface", (Class<?>[]) new Class[]{IBinder.class}, iBinder2);
            if (a2 != null) {
                e.a(a2, "setColorfulLight", (Class<?>[]) new Class[]{String.class, Integer.TYPE, Integer.TYPE}, context.getPackageName(), Integer.valueOf(i), Integer.valueOf(B.j()));
                Log.i("ColorLightManager", "mode " + i + " setColorfulLight");
            }
        } catch (Exception e) {
            Log.e("ColorLightManager", e.toString());
        }
    }

    public static void b(Context context) {
        if (b.a(context) && b.c(context) && b.d(context)) {
            a(context, 6);
        }
    }
}
