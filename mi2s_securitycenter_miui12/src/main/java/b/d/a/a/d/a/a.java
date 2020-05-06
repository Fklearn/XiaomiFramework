package b.d.a.a.d.a;

import java.util.regex.Pattern;

public class a {

    /* renamed from: a  reason: collision with root package name */
    public static Pattern f2107a = Pattern.compile("(\\d+\\.\\d+|[a-zA-Z0-9]+)");

    /* renamed from: b  reason: collision with root package name */
    private static final char[] f2108b = {'+', '#', '&', '.', '_', '-'};

    public static boolean a(char c2) {
        return b(c2) || e(c2) || d(c2) || c(c2);
    }

    public static boolean b(char c2) {
        return c2 >= 19968 && c2 <= 40869;
    }

    public static boolean c(char c2) {
        for (char c3 : f2108b) {
            if (c2 == c3) {
                return true;
            }
        }
        return false;
    }

    public static boolean d(char c2) {
        return c2 >= '0' && c2 <= '9';
    }

    public static boolean e(char c2) {
        if (c2 < 'A' || c2 > 'Z') {
            return c2 >= 'a' && c2 <= 'z';
        }
        return true;
    }

    public static char f(char c2) {
        int i;
        if (c2 == 12288) {
            return ' ';
        }
        if (c2 > 65280 && c2 < 65375) {
            i = c2 - 65248;
        } else if (c2 < 'A' || c2 > 'Z') {
            return c2;
        } else {
            i = c2 + ' ';
        }
        return (char) i;
    }
}
