package b.d.e;

import java.util.ArrayList;

public class h {
    public static String a(String str) {
        int length = str.length() - 1;
        while (length >= 0) {
            char charAt = str.charAt(length);
            if (charAt != 0 && charAt != 10 && charAt != ' ' && charAt != 9) {
                break;
            }
            length--;
        }
        String substring = str.substring(0, length + 1);
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        int i = 0;
        int i2 = 0;
        while (i < substring.length()) {
            if (a(substring.charAt(i))) {
                arrayList.add(substring.substring(i2, i));
                arrayList2.add(Character.valueOf(substring.charAt(i)));
                i2 = i + 1;
            }
            i++;
        }
        if (i2 < i) {
            arrayList.add(substring.substring(i2, i));
        }
        boolean z = false;
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            String b2 = b((String) arrayList.get(i3));
            if (b2 != null) {
                arrayList.set(i3, b2);
                z = true;
            }
        }
        if (!z) {
            return substring;
        }
        StringBuilder sb = new StringBuilder();
        for (int i4 = 0; i4 < arrayList2.size(); i4++) {
            sb.append((String) arrayList.get(i4));
            sb.append(arrayList2.get(i4));
        }
        if (arrayList2.size() < arrayList.size()) {
            sb.append((String) arrayList.get(arrayList.size() - 1));
        }
        return sb.toString();
    }

    private static boolean a(char c2) {
        return c2 == ',' || c2 == '.' || c2 == ';' || c2 == ':' || c2 == '!' || c2 == 65292 || c2 == 12290 || c2 == 65307 || c2 == 12289 || c2 == 65306 || c2 == 65281;
    }

    private static String b(String str) {
        StringBuilder sb = new StringBuilder();
        boolean z = false;
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < str.length(); i3++) {
            if (i.c(str.charAt(i3))) {
                i2++;
                sb.append(str.charAt(i3));
                if ((i3 == 0 || !i.c(str.charAt(i3 - 1))) && (i3 == str.length() - 1 || !i.c(str.charAt(i3 + 1)))) {
                    i++;
                }
            }
        }
        if (i >= 3 && (((double) i) * 1.0d) / ((double) i2) >= 0.5d) {
            z = true;
        }
        if (z) {
            return sb.toString();
        }
        return null;
    }
}
