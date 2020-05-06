package b.c.a.c;

import java.util.Comparator;

class e implements Comparator<String> {
    e() {
    }

    /* renamed from: a */
    public int compare(String str, String str2) {
        return str.substring(0, str.lastIndexOf("_")).compareTo(str2.substring(0, str2.lastIndexOf("_")));
    }
}
