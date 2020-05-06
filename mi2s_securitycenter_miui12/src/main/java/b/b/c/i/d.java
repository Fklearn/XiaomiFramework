package b.b.c.i;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import b.b.c.j.v;
import com.miui.securitycenter.R;

public class d {

    /* renamed from: a  reason: collision with root package name */
    private static d f1745a;

    /* renamed from: b  reason: collision with root package name */
    private Context f1746b;

    /* renamed from: c  reason: collision with root package name */
    private NotificationManager f1747c;

    private d(Context context) {
        this.f1746b = context;
        this.f1747c = (NotificationManager) context.getSystemService("notification");
    }

    public static synchronized d a(Context context) {
        d dVar;
        synchronized (d.class) {
            if (f1745a == null) {
                f1745a = new d(context.getApplicationContext());
            }
            dVar = f1745a;
        }
        return dVar;
    }

    public void a(int i) {
        this.f1747c.cancel(i);
    }

    public void a(int i, String str, String str2, PendingIntent pendingIntent) {
        Notification.Builder a2 = v.a(this.f1746b, "com.miui.securitycenter");
        a2.setContentTitle(str);
        a2.setContentText(str2);
        a2.setSmallIcon(R.drawable.security_small_icon);
        a2.setWhen(System.currentTimeMillis());
        a2.setContentIntent(pendingIntent);
        a2.setAutoCancel(true);
        Notification build = a2.build();
        build.tickerText = str + ":" + str2;
        build.flags = build.flags | 16;
        v.a(this.f1747c, "com.miui.securitycenter", this.f1746b.getResources().getString(R.string.notify_channel_name_security), 3);
        this.f1747c.notify(i, build);
    }
}
