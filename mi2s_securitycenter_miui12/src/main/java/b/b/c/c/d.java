package b.b.c.c;

import android.content.Context;

public class d {

    /* renamed from: a  reason: collision with root package name */
    public static Context f1632a;

    public static Context a() {
        Context context = f1632a;
        if (context != null) {
            return context;
        }
        throw new RuntimeException("Please invoke setContext before call this");
    }

    public static void a(Context context) {
        f1632a = context;
    }
}
