package b.d.a.a.a.a.a;

import b.d.a.a.a.a;
import b.d.a.a.a.a.b;
import java.lang.reflect.Array;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class c {

    /* renamed from: a  reason: collision with root package name */
    private static Map<a, b> f2085a = new ConcurrentHashMap();

    public static b a(a aVar) {
        if (f2085a.containsKey(aVar)) {
            return f2085a.get(aVar);
        }
        int[] b2 = b.d.a.a.e.a.b(b.d.a.a.a.c.f2090c + "classify/" + aVar.toString().toLowerCase() + "/id.bin");
        float[][] fArr = (float[][]) Array.newInstance(float.class, new int[]{2, b2.length});
        b.d.a.a.b.c[] cVarArr = new b.d.a.a.b.c[2];
        fArr[0] = b.d.a.a.e.a.a(b.d.a.a.a.c.f2090c + "classify/" + aVar.toString().toLowerCase() + "/weight1.bin");
        fArr[1] = b.d.a.a.e.a.a(b.d.a.a.a.c.f2090c + "classify/" + aVar.toString().toLowerCase() + "/weight2.bin");
        float[] fArr2 = null;
        float f = fArr[0][b2.length - 1];
        int length = b2.length - 1;
        while (length >= 0) {
            if (f != fArr[0][length]) {
                fArr2 = new float[(length + 1)];
                while (length >= 0) {
                    fArr2[length] = fArr[0][length];
                    length--;
                }
            }
            length--;
        }
        cVarArr[0] = new b.d.a.a.b.b(b.d.a.a.a.a.a.a.a.a(), b2, fArr2, f);
        cVarArr[1] = new b.d.a.a.b.b(b.d.a.a.a.a.a.a.a.a(), b2, fArr[1]);
        b bVar = new b(cVarArr);
        f2085a.put(aVar, bVar);
        return bVar;
    }

    public static boolean a() {
        f2085a = new ConcurrentHashMap();
        return true;
    }
}
