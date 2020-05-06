package b.b.a.b;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private static volatile b f1317a;

    /* renamed from: b  reason: collision with root package name */
    private Toast f1318b;

    /* renamed from: c  reason: collision with root package name */
    private long f1319c;

    private b a(Context context, String str, int i) {
        if (!TextUtils.isEmpty(str)) {
            a();
            this.f1318b = Toast.makeText(context, str, i);
        }
        return this;
    }

    public static b b() {
        if (f1317a == null) {
            synchronized (b.class) {
                if (f1317a == null) {
                    f1317a = new b();
                }
            }
        }
        return f1317a;
    }

    private b c() {
        Toast toast = this.f1318b;
        if (toast == null) {
            return null;
        }
        toast.show();
        this.f1319c = 0;
        return this;
    }

    public b a(Context context, int i) {
        a(context, context.getString(i), 2000);
        return c();
    }

    public void a() {
        if (f1317a != null && f1317a.f1318b != null) {
            if (f1317a.f1319c == 0 || (f1317a.f1318b != null && ((long) f1317a.f1318b.getDuration()) < System.currentTimeMillis() - f1317a.f1319c)) {
                f1317a.f1318b.cancel();
                f1317a.f1318b = null;
            }
        }
    }
}
