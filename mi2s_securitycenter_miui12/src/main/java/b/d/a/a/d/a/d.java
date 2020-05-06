package b.d.a.a.d.a;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class d {

    /* renamed from: a  reason: collision with root package name */
    private static h f2121a = h.a();

    /* renamed from: b  reason: collision with root package name */
    private static b.d.a.a.d.a.a.a f2122b = b.d.a.a.d.a.a.a.a();

    public enum a {
        INDEX,
        SEARCH
    }

    private Map<Integer, f<Integer>> a(String str, Map<Integer, List<Integer>> map) {
        int length = str.length();
        HashMap hashMap = new HashMap();
        hashMap.put(Integer.valueOf(length), new f(0, 0.0d));
        while (true) {
            length--;
            if (length <= -1) {
                return hashMap;
            }
            f fVar = null;
            Iterator it = map.get(Integer.valueOf(length)).iterator();
            while (it.hasNext()) {
                K k = (Integer) it.next();
                double doubleValue = f2121a.b(str.substring(length, k.intValue() + 1)).doubleValue() + ((f) hashMap.get(Integer.valueOf(k.intValue() + 1))).f2129b;
                if (fVar == null) {
                    fVar = new f(k, doubleValue);
                } else if (fVar.f2129b < doubleValue) {
                    fVar.f2129b = doubleValue;
                    fVar.f2128a = k;
                }
            }
            hashMap.put(Integer.valueOf(length), fVar);
        }
    }

    private Map<Integer, List<Integer>> b(String str) {
        List list;
        HashMap hashMap = new HashMap();
        b b2 = f2121a.b();
        char[] charArray = str.toCharArray();
        int length = charArray.length;
        int i = 0;
        while (true) {
            int i2 = i;
            while (i < length) {
                c a2 = b2.a(charArray, i, (i2 - i) + 1);
                if (a2.b() || a2.a()) {
                    if (a2.a()) {
                        if (!hashMap.containsKey(Integer.valueOf(i))) {
                            list = new ArrayList();
                            hashMap.put(Integer.valueOf(i), list);
                        } else {
                            list = (List) hashMap.get(Integer.valueOf(i));
                        }
                        list.add(Integer.valueOf(i2));
                    }
                    i2++;
                    if (i2 >= length) {
                    }
                }
                i++;
            }
            break;
        }
        for (int i3 = 0; i3 < length; i3++) {
            if (!hashMap.containsKey(Integer.valueOf(i3))) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(Integer.valueOf(i3));
                hashMap.put(Integer.valueOf(i3), arrayList);
            }
        }
        return hashMap;
    }

    public List<String> a(String str) {
        ArrayList arrayList = new ArrayList();
        int length = str.length();
        Map<Integer, f<Integer>> a2 = a(str, b(str));
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < length) {
            int intValue = ((Integer) a2.get(Integer.valueOf(i)).f2128a).intValue() + 1;
            String substring = str.substring(i, intValue);
            if (intValue - i == 1) {
                sb.append(substring);
            } else {
                if (sb.length() > 0) {
                    String sb2 = sb.toString();
                    StringBuilder sb3 = new StringBuilder();
                    if (sb2.length() != 1 && !f2121a.a(sb2)) {
                        f2122b.a(sb2, arrayList);
                    } else {
                        arrayList.add(sb2);
                    }
                    sb = sb3;
                }
                arrayList.add(substring);
            }
            i = intValue;
        }
        String sb4 = sb.toString();
        if (sb4.length() > 0) {
            if (sb4.length() != 1 && !f2121a.a(sb4)) {
                f2122b.a(sb4, arrayList);
            } else {
                arrayList.add(sb4);
            }
        }
        return arrayList;
    }

    public List<g> a(String str, a aVar) {
        int i;
        g gVar;
        ArrayList arrayList = new ArrayList();
        StringBuilder sb = new StringBuilder();
        int i2 = 0;
        for (int i3 = 0; i3 < str.length(); i3++) {
            char f = a.f(str.charAt(i3));
            if (a.a(f)) {
                sb.append(f);
            } else {
                if (sb.length() > 0) {
                    if (aVar == a.SEARCH) {
                        for (String next : a(sb.toString())) {
                            int length = next.length() + i2;
                            arrayList.add(new g(next, i2, length));
                            i2 = length;
                        }
                    } else {
                        for (String next2 : a(sb.toString())) {
                            if (next2.length() > 2) {
                                for (int i4 = 0; i4 < next2.length() - 1; i4++) {
                                    String substring = next2.substring(i4, i4 + 2);
                                    if (f2121a.a(substring)) {
                                        int i5 = i2 + i4;
                                        arrayList.add(new g(substring, i5, i5 + 2));
                                    }
                                }
                            }
                            if (next2.length() > 3) {
                                for (int i6 = 0; i6 < next2.length() - 2; i6++) {
                                    String substring2 = next2.substring(i6, i6 + 3);
                                    if (f2121a.a(substring2)) {
                                        int i7 = i2 + i6;
                                        arrayList.add(new g(substring2, i7, i7 + 3));
                                    }
                                }
                            }
                            int length2 = next2.length() + i2;
                            arrayList.add(new g(next2, i2, length2));
                            i2 = length2;
                        }
                    }
                    sb = new StringBuilder();
                    i2 = i3;
                }
                int i8 = i3 + 1;
                if (f2121a.a(str.substring(i3, i8))) {
                    i = i2 + 1;
                    gVar = new g(str.substring(i3, i8), i2, i);
                } else {
                    i = i2 + 1;
                    gVar = new g(str.substring(i3, i8), i2, i);
                }
                arrayList.add(gVar);
                i2 = i;
            }
        }
        if (sb.length() > 0) {
            if (aVar == a.SEARCH) {
                for (String next3 : a(sb.toString())) {
                    int length3 = next3.length() + i2;
                    arrayList.add(new g(next3, i2, length3));
                    i2 = length3;
                }
            } else {
                for (String next4 : a(sb.toString())) {
                    if (next4.length() > 2) {
                        for (int i9 = 0; i9 < next4.length() - 1; i9++) {
                            String substring3 = next4.substring(i9, i9 + 2);
                            if (f2121a.a(substring3)) {
                                int i10 = i2 + i9;
                                arrayList.add(new g(substring3, i10, i10 + 2));
                            }
                        }
                    }
                    if (next4.length() > 3) {
                        for (int i11 = 0; i11 < next4.length() - 2; i11++) {
                            String substring4 = next4.substring(i11, i11 + 3);
                            if (f2121a.a(substring4)) {
                                int i12 = i2 + i11;
                                arrayList.add(new g(substring4, i12, i12 + 3));
                            }
                        }
                    }
                    int length4 = next4.length() + i2;
                    arrayList.add(new g(next4, i2, length4));
                    i2 = length4;
                }
            }
        }
        return arrayList;
    }
}
