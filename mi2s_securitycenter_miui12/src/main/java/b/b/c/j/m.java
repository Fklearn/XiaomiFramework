package b.b.c.j;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import b.b.o.g.d;

public class m {
    public static void a(Context context, Intent intent) {
        String str;
        if (Build.VERSION.SDK_INT > 25) {
            try {
                Log.d("ForegroundServiceUtil", "v26 startForegroundService");
                d.b("ForegroundServiceUtil", context, "startForegroundService", new Class[]{Intent.class}, intent);
                return;
            } catch (Exception e) {
                e = e;
                str = "startForegroundService error";
            }
        } else {
            try {
                Log.d("ForegroundServiceUtil", "default startService");
                context.startService(intent);
                return;
            } catch (Exception e2) {
                e = e2;
                str = "startService error";
            }
        }
        Log.e("ForegroundServiceUtil", str, e);
    }
}
