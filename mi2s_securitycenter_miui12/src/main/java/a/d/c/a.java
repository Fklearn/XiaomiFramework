package a.d.c;

import android.os.Build;
import android.os.Trace;
import android.util.Log;
import androidx.annotation.NonNull;
import java.lang.reflect.Method;

public final class a {

    /* renamed from: a  reason: collision with root package name */
    private static long f117a;

    /* renamed from: b  reason: collision with root package name */
    private static Method f118b;

    /* renamed from: c  reason: collision with root package name */
    private static Method f119c;

    /* renamed from: d  reason: collision with root package name */
    private static Method f120d;
    private static Method e;

    static {
        int i = Build.VERSION.SDK_INT;
        if (i >= 18 && i < 29) {
            try {
                f117a = Trace.class.getField("TRACE_TAG_APP").getLong((Object) null);
                f118b = Trace.class.getMethod("isTagEnabled", new Class[]{Long.TYPE});
                f119c = Trace.class.getMethod("asyncTraceBegin", new Class[]{Long.TYPE, String.class, Integer.TYPE});
                f120d = Trace.class.getMethod("asyncTraceEnd", new Class[]{Long.TYPE, String.class, Integer.TYPE});
                e = Trace.class.getMethod("traceCounter", new Class[]{Long.TYPE, String.class, Integer.TYPE});
            } catch (Exception e2) {
                Log.i("TraceCompat", "Unable to initialize via reflection.", e2);
            }
        }
    }

    public static void a() {
        if (Build.VERSION.SDK_INT >= 18) {
            Trace.endSection();
        }
    }

    public static void a(@NonNull String str) {
        if (Build.VERSION.SDK_INT >= 18) {
            Trace.beginSection(str);
        }
    }
}
