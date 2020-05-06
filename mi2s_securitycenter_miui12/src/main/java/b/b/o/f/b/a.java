package b.b.o.f.b;

import android.content.Context;
import android.os.Build;

public abstract class a {
    public static a a(Context context) {
        return Build.VERSION.SDK_INT >= 22 ? c.b(context) : b.b(context);
    }

    public abstract void a(int i);
}
