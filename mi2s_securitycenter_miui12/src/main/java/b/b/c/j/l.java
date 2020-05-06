package b.b.c.j;

import android.util.Log;
import android.view.View;
import d.a.a.a;
import d.a.b;
import d.a.f;
import d.a.j;

public class l {
    public static void a(View view, float f) {
        if (view != null && A.a()) {
            try {
                f a2 = b.a(view);
                a2.touch().a(f, new j.a[0]);
                a2.touch().a(view, true, new a[0]);
            } catch (Throwable unused) {
                Log.e("FolmeUtils", "not support folme");
            }
        }
    }

    public static void a(View view, View view2) {
        if (view != null && view2 != null && A.a()) {
            try {
                b.a(view2).touch().b(view, new a[0]);
            } catch (Throwable unused) {
                Log.e("FolmeUtils", "not support folme");
            }
        }
    }

    public static boolean a(View view) {
        if (view != null && A.a()) {
            try {
                b.a(view).touch().a(view, new a[0]);
                return true;
            } catch (Throwable unused) {
                Log.e("FolmeUtils", "not support folme");
            }
        }
        return false;
    }

    public static void b(View view) {
        if (view != null && A.a()) {
            try {
                b.a(view).touch().a(view, true, new a[0]);
            } catch (Throwable unused) {
                Log.e("FolmeUtils", "not support folme");
            }
        }
    }
}
