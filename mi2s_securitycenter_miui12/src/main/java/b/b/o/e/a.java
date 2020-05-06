package b.b.o.e;

import b.b.o.g.e;

public class a {
    private static Class<?> a() {
        return Class.forName("android.os.SystemProperties");
    }

    public static String a(String str, String str2) {
        try {
            return (String) e.a(a(), String.class, "get", (Class<?>[]) new Class[]{String.class, String.class}, str, str2);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
