package b.d.e;

import android.content.Context;
import android.util.Log;
import b.d.e.a.a;
import java.io.IOException;
import java.io.InputStream;

public class g {

    /* renamed from: a  reason: collision with root package name */
    private static final String f2215a = "b.d.e.g";

    /* renamed from: b  reason: collision with root package name */
    private a f2216b;

    public void a() {
        this.f2216b = null;
    }

    public void a(Context context) {
        this.f2216b = new a();
        try {
            InputStream a2 = c.a(context, "dict");
            this.f2216b.a(a2);
            a2.close();
        } catch (IOException unused) {
            Log.d(f2215a, "init failed");
        }
    }

    public String[] a(String str) {
        return this.f2216b.a(str);
    }

    public boolean b(String str) {
        return this.f2216b.b(str);
    }
}
