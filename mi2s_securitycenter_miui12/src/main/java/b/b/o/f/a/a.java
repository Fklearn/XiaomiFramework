package b.b.o.f.a;

import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

public abstract class a {
    public static a a() {
        int i = Build.VERSION.SDK_INT;
        return i >= 24 ? d.a() : i >= 21 ? c.a() : b.a();
    }

    public abstract boolean a(IBinder iBinder, int i, Intent intent);
}
