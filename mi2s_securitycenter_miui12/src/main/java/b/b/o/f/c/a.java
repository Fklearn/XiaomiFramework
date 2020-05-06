package b.b.o.f.c;

import android.content.Context;
import android.os.Build;

public abstract class a {
    public static a a(Context context) {
        return Build.VERSION.SDK_INT >= 21 ? c.a(context) : b.a(context);
    }

    public abstract void a(boolean z);

    public abstract boolean a();
}
