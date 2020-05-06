package b.b.a.e;

import android.content.ContentProvider;
import android.net.Uri;
import android.os.IBinder;
import android.os.UserHandle;
import b.b.o.g.d;
import miui.util.Log;

public class q {
    public static int a() {
        try {
            IBinder iBinder = (IBinder) d.a("UserUtil", Class.forName("android.os.ServiceManager"), "getService", (Class<?>[]) new Class[]{String.class}, "security");
            d.a("UserUtil", d.a("UserUtil", Class.forName("miui.security.ISecurityManager$Stub"), "asInterface", (Class<?>[]) new Class[]{IBinder.class}, iBinder), "getCurrentUserId", (Class<?>[]) new Class[0], new Object[0]);
        } catch (Exception e) {
            Log.e("UserUtil", "UserUtil of antispam exception!", e);
        }
        return 0;
    }

    public static Uri a(Uri uri, int i) {
        if (i == -1) {
            return uri;
        }
        return (Uri) d.a("UserUtil", (Class<?>) ContentProvider.class, "maybeAddUserId", (Class<?>[]) new Class[]{Uri.class, Integer.TYPE}, uri, Integer.valueOf(i));
    }

    public static final boolean b() {
        return UserHandle.myUserId() == 0;
    }
}
