package b.d.a.a.a.a.a.a;

import b.d.a.a.b.b;
import b.d.a.a.d.a.d;
import b.d.a.a.d.a.g;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class c extends a {
    public b.d.a.a.b.c a(String str) {
        if (str == null || str.equals("")) {
            throw new RuntimeException("smsBody is null or empty!");
        }
        HashMap hashMap = new HashMap();
        List<g> a2 = a.f2082b.a(str, d.a.INDEX);
        int i = 0;
        if (a2.size() > 20) {
            a2 = a2.subList(0, 21);
        }
        for (g next : a2) {
            if (a.f2081a.containsKey(next.f2130a)) {
                Integer num = a.f2081a.get(next.f2130a);
                hashMap.put(num, Integer.valueOf((hashMap.containsKey(num) ? ((Integer) hashMap.get(num)).intValue() : 0) + 1));
            }
        }
        int[] iArr = new int[hashMap.size()];
        float[] fArr = new float[hashMap.size()];
        for (Map.Entry entry : hashMap.entrySet()) {
            iArr[i] = ((Integer) entry.getKey()).intValue();
            fArr[i] = (float) ((Integer) entry.getValue()).intValue();
            i++;
        }
        return new b(a.f2083c, iArr, fArr);
    }
}
