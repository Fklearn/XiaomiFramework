package b.b.c.j;

import android.content.Context;
import android.graphics.drawable.Drawable;
import b.b.o.g.c;

public class C {
    public static Drawable a(Context context, Drawable drawable, int i) {
        c.a a2 = c.a.a("miui.securityspace.XSpaceUserHandle");
        a2.b("getXSpaceIcon", new Class[]{Context.class, Drawable.class, Integer.TYPE}, context, drawable, Integer.valueOf(i));
        return (Drawable) a2.d();
    }

    public static boolean a(int i) {
        return B.c(i) == 999;
    }

    public static boolean b(int i) {
        c.a a2 = c.a.a("miui.securityspace.XSpaceUserHandle");
        a2.b("isXSpaceUserId", new Class[]{Integer.TYPE}, Integer.valueOf(i));
        return a2.a();
    }
}
