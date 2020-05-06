package b.b.c.j;

import android.util.Log;
import android.view.View;
import b.b.o.g.e;
import miui.app.ActionBar;

/* renamed from: b.b.c.j.b  reason: case insensitive filesystem */
public class C0195b {
    public static void a(ActionBar actionBar, int i) {
        try {
            e.a((Object) actionBar, "setExpandState", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(i));
        } catch (Exception e) {
            Log.e("ActionBarUtils", "reflect error when set actionbar expand state", e);
        }
    }

    public static boolean a(ActionBar actionBar, View view) {
        try {
            e.a((Object) actionBar, "setEndView", (Class<?>[]) new Class[]{View.class}, view);
            return true;
        } catch (Exception e) {
            Log.e("ActionBarUtils", "reflect error when set actionbar endview", e);
            return false;
        }
    }

    public static boolean a(ActionBar actionBar, boolean z) {
        try {
            e.a((Object) actionBar, "setResizable", (Class<?>[]) new Class[]{Boolean.TYPE}, Boolean.valueOf(z));
            return true;
        } catch (Exception e) {
            Log.e("ActionBarUtils", "reflect error when set actionbar endview", e);
            return false;
        }
    }
}
