package b.d.e;

import java.util.ArrayList;
import java.util.List;

public class i {
    public static double a(double d2) {
        return Math.log(1.0d - Math.exp(d2));
    }

    public static boolean a(char c2) {
        return 19968 <= c2 && c2 <= 40959;
    }

    public static boolean a(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!a(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean a(List<String> list) {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<String> arrayList2 = new ArrayList<>();
        int i = 0;
        for (String next : list) {
            i += next.length();
            int i2 = 0;
            boolean b2 = b(next.charAt(0));
            for (int i3 = 1; i3 < next.length(); i3++) {
                if (b2 != b(next.charAt(i3))) {
                    if (b2) {
                        arrayList.add(next.substring(i2, i3));
                    } else {
                        arrayList2.add(next.substring(i2, i3));
                    }
                    b2 = !b2;
                    i2 = i3;
                }
            }
            if (b2) {
                arrayList.add(next.substring(i2));
            } else {
                arrayList2.add(next.substring(i2));
            }
        }
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        for (String str : arrayList) {
            if (i4 < str.length()) {
                i4 = str.length();
            }
            if (str.length() > 3) {
                i5++;
            }
            i6 += str.length();
        }
        int i7 = 0;
        for (String length : arrayList2) {
            if (length.length() > 1) {
                i7++;
            }
        }
        return (arrayList.size() > i / 4 || i6 * 3 < i) && i5 <= 1 && i4 < 8 && i7 * 2 > arrayList2.size();
    }

    public static String b(String str) {
        if (str.indexOf("//") != -1) {
            str = str.substring(str.indexOf("//") + 2);
        }
        return str.indexOf("/") != -1 ? str.substring(0, str.indexOf("/")) : str;
    }

    public static boolean b(char c2) {
        return c(c2) || c2 == '.' || c2 == ' ' || c2 == ' ';
    }

    public static boolean c(char c2) {
        if (19968 <= c2 && c2 <= 40959) {
            return true;
        }
        if ('0' <= c2 && c2 <= '9') {
            return true;
        }
        if ('a' <= c2 && c2 <= 'z') {
            return true;
        }
        if ('A' > c2 || c2 > 'Z') {
            return 65296 <= c2 && c2 <= 65305;
        }
        return true;
    }
}
