package b.d.d.b;

import b.d.d.a.a;
import b.d.d.e.c;
import java.util.HashMap;
import java.util.Map;

public class b implements a {
    public c a(String str) {
        HashMap hashMap = new HashMap();
        double[] dArr = new double[a.f2153a.size()];
        int i = 0;
        while (i < str.length()) {
            int i2 = i + 1;
            String substring = str.substring(i, i2);
            hashMap.put(str.substring(i, i2), Integer.valueOf((hashMap.containsKey(substring) ? ((Integer) hashMap.get(substring)).intValue() : 0) + 1));
            i = i2;
        }
        for (Map.Entry entry : hashMap.entrySet()) {
            int i3 = 3096;
            if (a.f2153a.containsKey(entry.getKey())) {
                i3 = a.f2153a.get(entry.getKey()).intValue();
            }
            double d2 = 1.0d;
            if (b.d.d.a.b.f2154a.containsKey(entry.getKey())) {
                d2 = b.d.d.a.b.f2154a.get(entry.getKey()).doubleValue();
            }
            dArr[i3] = ((double) ((Integer) entry.getValue()).intValue()) * d2;
        }
        return new b.d.d.e.a(dArr);
    }
}
