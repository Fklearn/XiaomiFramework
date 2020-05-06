package b.d.e.c;

public class a extends d {

    /* renamed from: a  reason: collision with root package name */
    private static final String[] f2189a = {"Common", "100+5", "106Short", "106Common", "106Long", "9Pre5", "Other", "Null"};

    public static int a(String str) {
        if (str == null || str.length() == 0) {
            return 7;
        }
        if (str.charAt(0) != '1' || str.length() < 5) {
            return (str.charAt(0) == '9' && str.length() == 5) ? 5 : 6;
        }
        char charAt = str.charAt(1);
        if (str.length() == 11 && (charAt == '3' || charAt == '5' || charAt == '8' || (charAt == '4' && str.charAt(2) == '7'))) {
            return 0;
        }
        if (charAt != '0') {
            return 6;
        }
        if (str.length() == 5 && str.charAt(2) == '0') {
            return 1;
        }
        if (str.charAt(2) != '6') {
            return 6;
        }
        if (str.length() < 9) {
            return 2;
        }
        return str.length() < 12 ? 3 : 4;
    }

    public static boolean a(int i) {
        return i == 0;
    }

    public static int b() {
        return f2189a.length;
    }
}
