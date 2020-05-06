package d.g.b;

import android.util.Log;
import java.util.Locale;

class c {

    /* renamed from: a  reason: collision with root package name */
    private static final boolean f8851a = Log.isLoggable("OverScroll", 3);

    /* renamed from: b  reason: collision with root package name */
    private static final boolean f8852b = Log.isLoggable("OverScroll", 2);

    public static void a(String str) {
        if (f8851a) {
            Log.d("OverScroll", str);
        }
    }

    public static void a(String str, Object... objArr) {
        if (f8851a) {
            Log.d("OverScroll", String.format(Locale.US, str, objArr));
        }
    }

    public static void b(String str) {
        if (f8852b) {
            Log.v("OverScroll", str);
        }
    }

    public static void b(String str, Object... objArr) {
        if (f8852b) {
            Log.v("OverScroll", String.format(Locale.US, str, objArr));
        }
    }
}
