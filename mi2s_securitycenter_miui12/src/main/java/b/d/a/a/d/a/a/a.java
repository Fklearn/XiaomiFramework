package b.d.a.a.d.a.a;

import b.d.a.a.a.c;
import b.d.a.a.d.a.e;
import b.d.a.a.d.a.f;
import com.google.android.exoplayer2.C;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private static a f2109a;

    /* renamed from: b  reason: collision with root package name */
    private static char[] f2110b = {'B', 'M', 'E', 'S'};

    /* renamed from: c  reason: collision with root package name */
    private static Map<Character, Map<Character, Double>> f2111c;

    /* renamed from: d  reason: collision with root package name */
    private static Map<Character, Double> f2112d;
    private static Map<Character, Map<Character, Double>> e;
    private static Map<Character, char[]> f;

    private a() {
    }

    public static synchronized a a() {
        a aVar;
        synchronized (a.class) {
            if (f2109a == null) {
                f2109a = new a();
                f2109a.b();
            }
            aVar = f2109a;
        }
        return aVar;
    }

    private void b() {
        f = new HashMap();
        f.put('B', new char[]{'E', 'S'});
        f.put('M', new char[]{'M', 'B'});
        f.put('S', new char[]{'S', 'E'});
        f.put('E', new char[]{'B', 'M'});
        f2112d = new HashMap();
        f2112d.put('B', Double.valueOf(-0.26268660809250016d));
        Map<Character, Double> map = f2112d;
        Double valueOf = Double.valueOf(-3.14E100d);
        map.put('E', valueOf);
        f2112d.put('M', valueOf);
        f2112d.put('S', Double.valueOf(-1.4652633398537678d));
        e = new HashMap();
        HashMap hashMap = new HashMap();
        hashMap.put('E', Double.valueOf(-0.51082562376599d));
        hashMap.put('M', Double.valueOf(-0.916290731874155d));
        e.put('B', hashMap);
        HashMap hashMap2 = new HashMap();
        hashMap2.put('B', Double.valueOf(-0.5897149736854513d));
        hashMap2.put('S', Double.valueOf(-0.8085250474669937d));
        e.put('E', hashMap2);
        HashMap hashMap3 = new HashMap();
        hashMap3.put('E', Double.valueOf(-0.33344856811948514d));
        hashMap3.put('M', Double.valueOf(-1.2603623820268226d));
        e.put('M', hashMap3);
        HashMap hashMap4 = new HashMap();
        hashMap4.put('B', Double.valueOf(-0.7211965654669841d));
        hashMap4.put('S', Double.valueOf(-0.6658631448798212d));
        e.put('S', hashMap4);
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(c.f2090c + "prob_emit.txt"), C.UTF8_NAME));
            f2111c = new HashMap();
            HashMap hashMap5 = null;
            for (String readLine = bufferedReader.readLine(); readLine != null; readLine = bufferedReader.readLine()) {
                if (readLine.length() == 1) {
                    hashMap5 = new HashMap();
                    f2111c.put(Character.valueOf(readLine.charAt(0)), hashMap5);
                } else {
                    hashMap5.put(Character.valueOf(readLine.charAt(0)), Double.valueOf(readLine.substring(2)));
                }
            }
            bufferedReader.close();
        } catch (IOException e2) {
            throw new RuntimeException(e2);
        }
    }

    private void b(String str, List<String> list) {
        Matcher matcher = b.d.a.a.d.a.a.f2107a.matcher(str);
        int i = 0;
        while (matcher.find()) {
            if (matcher.start() > i) {
                list.add(str.substring(i, matcher.start()));
            }
            list.add(matcher.group());
            i = matcher.end();
        }
        if (i < str.length()) {
            list.add(str.substring(i));
        }
    }

    private void c(String str, List<String> list) {
        double d2;
        String substring;
        int i;
        char[] cArr;
        String str2 = str;
        List<String> list2 = list;
        Vector vector = new Vector();
        HashMap hashMap = new HashMap();
        vector.add(new HashMap());
        char[] cArr2 = f2110b;
        int length = cArr2.length;
        int i2 = 0;
        int i3 = 0;
        while (true) {
            d2 = -3.14E100d;
            if (i3 >= length) {
                break;
            }
            char c2 = cArr2[i3];
            Double d3 = (Double) f2111c.get(Character.valueOf(c2)).get(Character.valueOf(str2.charAt(0)));
            if (d3 == null) {
                d3 = Double.valueOf(-3.14E100d);
            }
            ((Map) vector.get(0)).put(Character.valueOf(c2), Double.valueOf(f2112d.get(Character.valueOf(c2)).doubleValue() + d3.doubleValue()));
            hashMap.put(Character.valueOf(c2), new e(Character.valueOf(c2), (e) null));
            i3++;
        }
        HashMap hashMap2 = hashMap;
        int i4 = 1;
        while (i4 < str.length()) {
            HashMap hashMap3 = new HashMap();
            vector.add(hashMap3);
            HashMap hashMap4 = new HashMap();
            char[] cArr3 = f2110b;
            int length2 = cArr3.length;
            int i5 = i2;
            while (i5 < length2) {
                char c3 = cArr3[i5];
                Double d4 = (Double) f2111c.get(Character.valueOf(c3)).get(Character.valueOf(str2.charAt(i4)));
                if (d4 == null) {
                    d4 = Double.valueOf(d2);
                }
                char[] cArr4 = f.get(Character.valueOf(c3));
                int length3 = cArr4.length;
                int i6 = 0;
                f fVar = null;
                while (i6 < length3) {
                    char c4 = cArr4[i6];
                    char[] cArr5 = cArr4;
                    int i7 = length3;
                    Double d5 = (Double) e.get(Character.valueOf(c4)).get(Character.valueOf(c3));
                    if (d5 == null) {
                        d5 = Double.valueOf(-3.14E100d);
                    }
                    Double valueOf = Double.valueOf(d5.doubleValue() + d4.doubleValue() + ((Double) ((Map) vector.get(i4 - 1)).get(Character.valueOf(c4))).doubleValue());
                    if (fVar == null) {
                        cArr = cArr3;
                        i = length2;
                        fVar = new f(Character.valueOf(c4), valueOf.doubleValue());
                    } else {
                        cArr = cArr3;
                        i = length2;
                        if (fVar.f2129b <= valueOf.doubleValue()) {
                            fVar.f2129b = valueOf.doubleValue();
                            fVar.f2128a = Character.valueOf(c4);
                        }
                    }
                    i6++;
                    cArr4 = cArr5;
                    length3 = i7;
                    cArr3 = cArr;
                    length2 = i;
                }
                char[] cArr6 = cArr3;
                int i8 = length2;
                hashMap3.put(Character.valueOf(c3), Double.valueOf(fVar.f2129b));
                hashMap4.put(Character.valueOf(c3), new e(Character.valueOf(c3), (e) hashMap2.get(fVar.f2128a)));
                i5++;
                d2 = -3.14E100d;
            }
            double d6 = d2;
            i4++;
            hashMap2 = hashMap4;
            i2 = 0;
        }
        double doubleValue = ((Double) ((Map) vector.get(str.length() - 1)).get('E')).doubleValue();
        double doubleValue2 = ((Double) ((Map) vector.get(str.length() - 1)).get('S')).doubleValue();
        Vector vector2 = new Vector(str.length());
        for (e eVar = (e) hashMap2.get(doubleValue < doubleValue2 ? 'S' : 'E'); eVar != null; eVar = eVar.f2127b) {
            vector2.add(eVar.f2126a);
        }
        Collections.reverse(vector2);
        int i9 = 0;
        int i10 = 0;
        for (int i11 = 0; i11 < str.length(); i11++) {
            char charValue = ((Character) vector2.get(i11)).charValue();
            if (charValue == 'B') {
                i10 = i11;
            } else {
                if (charValue == 'E') {
                    i9 = i11 + 1;
                    substring = str2.substring(i10, i9);
                } else if (charValue == 'S') {
                    i9 = i11 + 1;
                    substring = str2.substring(i11, i9);
                }
                list2.add(substring);
            }
        }
        if (i9 < str.length()) {
            list2.add(str2.substring(i9));
        }
    }

    public void a(String str, List<String> list) {
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (b.d.a.a.d.a.a.b(charAt)) {
                if (sb2.length() > 0) {
                    b(sb2.toString(), list);
                    sb2 = new StringBuilder();
                }
                sb.append(charAt);
            } else {
                if (sb.length() > 0) {
                    c(sb.toString(), list);
                    sb = new StringBuilder();
                }
                sb2.append(charAt);
            }
        }
        if (sb.length() > 0) {
            c(sb.toString(), list);
        } else {
            b(sb2.toString(), list);
        }
    }
}
