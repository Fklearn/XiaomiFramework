package b.b.o.c;

import android.content.Context;
import b.b.o.g.e;

public class a {
    public static boolean a(Context context, String str) {
        try {
            return ((Boolean) e.a(Class.forName("com.miui.enterprise.ApplicationHelper"), "shouldGrantPermission", (Class<?>[]) new Class[]{Context.class, String.class}, context, str)).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean a(Context context, String str, int i) {
        try {
            return ((Boolean) e.a(Class.forName("com.miui.enterprise.ApplicationHelper"), "shouldGrantPermission", (Class<?>[]) new Class[]{Context.class, String.class, Integer.TYPE}, context, str, Integer.valueOf(i))).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
