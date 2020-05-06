package b.b.b.c;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import b.b.c.j.d;
import b.b.c.j.x;
import com.miui.common.persistence.b;
import com.miui.securityscan.c.a;

public class g {

    /* renamed from: a  reason: collision with root package name */
    private static g f1498a;

    /* renamed from: b  reason: collision with root package name */
    private static final Object f1499b = new Object();
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public Context f1500c;

    private g(Context context) {
        this.f1500c = context.getApplicationContext();
    }

    public static synchronized g a(Context context) {
        g gVar;
        synchronized (g.class) {
            if (f1498a == null) {
                f1498a = new g(context);
            }
            gVar = f1498a;
        }
        return gVar;
    }

    public static boolean a() {
        return Build.VERSION.SDK_INT >= 21;
    }

    public static boolean b(Context context) {
        return c() && c(context);
    }

    private static boolean c() {
        return b.a("side_kick_status", true);
    }

    public static boolean c(Context context) {
        return x.e(context, "com.miui.voiceassist") >= 304005000;
    }

    public void a(long j) {
        d.a(new f(this, j));
    }

    public boolean b() {
        if (!b(this.f1500c) || !a()) {
            return false;
        }
        long currentTimeMillis = System.currentTimeMillis();
        long j = currentTimeMillis - this.f1500c.getSharedPreferences("av_sidekick_settings", 0).getLong("side_kick_last_time", currentTimeMillis);
        long a2 = b.a("side_kick_interval", 86400000);
        if (a.f7625a) {
            Log.d("SidekickSettings", "cur interval: " + j);
            Log.d("SidekickSettings", "min interval: " + a2);
        }
        return j == 0 || j > a2;
    }
}
