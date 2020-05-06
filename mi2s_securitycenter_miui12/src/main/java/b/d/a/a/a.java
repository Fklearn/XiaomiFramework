package b.d.a.a;

import b.d.a.a.a.c;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private static boolean f2074a = false;

    /* renamed from: b  reason: collision with root package name */
    private static boolean f2075b = false;

    public static boolean a() {
        f2074a = false;
        c.c();
        f2075b = false;
        return true;
    }

    public static boolean a(String str) {
        if (!str.endsWith("/")) {
            str = str + "/";
        }
        c.a(str);
        f2074a = true;
        b.d.d.a.a(str + "unlawful");
        f2075b = true;
        return true;
    }

    public static boolean a(String str, String str2) {
        if (f2074a) {
            return c.a(str, str2).booleanValue();
        }
        throw new RuntimeException("重要短信识别未初始化成功");
    }

    public static boolean b() {
        return f2074a;
    }

    public static boolean b(String str) {
        if (f2075b) {
            return b.d.d.a.b(str);
        }
        throw new RuntimeException("非法短信识别引擎未初始化成功");
    }

    public static boolean c() {
        return f2075b;
    }
}
