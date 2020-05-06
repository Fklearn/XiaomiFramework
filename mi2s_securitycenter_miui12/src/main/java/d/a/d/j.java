package d.a.d;

import android.util.Log;
import d.a.b.a;
import d.a.d;
import d.a.g.C0575b;
import d.a.g.C0576c;
import d.a.g.C0577d;
import d.a.h.c;

public class j {
    public static float a(d dVar, C0575b bVar, float f) {
        return bVar instanceof C0577d ? ((C0577d) bVar).getSpecificValue(f) : b(dVar, bVar, f);
    }

    private static void a(d dVar, a aVar, C0575b bVar) {
        boolean a2 = aVar.a(bVar, 1);
        if (bVar instanceof C0576c) {
            int d2 = aVar.d(bVar);
            if (a2) {
                d2 += dVar.getIntValue((C0576c) bVar);
            }
            dVar.setIntValue((C0576c) bVar, d2);
            return;
        }
        float a3 = aVar.a(dVar, bVar);
        if (a2) {
            a3 += dVar.getValue(bVar);
        }
        dVar.setValue(bVar, a3);
    }

    public static void a(d dVar, a aVar, long... jArr) {
        if (aVar != null && dVar != null) {
            long j = jArr.length > 0 ? jArr[0] : 0;
            for (C0575b next : aVar.d()) {
                if (aVar.e(next) && (j == 0 || aVar.a(next, j))) {
                    a(dVar, aVar, next);
                }
            }
        }
    }

    private static <T> void a(C0575b bVar, T t, T t2, String str) {
        String str2;
        StringBuilder sb = new StringBuilder();
        sb.append("setValues, ");
        sb.append(bVar.getName());
        sb.append(", fromValue = ");
        sb.append(t);
        sb.append(", toValue = ");
        sb.append(t2);
        if (str != null) {
            str2 = ", " + str;
        } else {
            str2 = "";
        }
        sb.append(str2);
        Log.d("miuix_anim", sb.toString());
    }

    static boolean a(d dVar, c cVar, h hVar, Number number, long j) {
        float value = dVar.getValue(hVar.f8707b);
        float a2 = a(dVar, hVar.f8707b, number.floatValue());
        if (d.a.i.a.a(j, 1)) {
            a2 += value;
        }
        if (hVar.c() || value != a2) {
            a(hVar.f8707b, Float.valueOf(value), Float.valueOf(a2), (String) null);
            cVar.a(value, a2);
            return true;
        }
        a(hVar.f8707b, Float.valueOf(value), Float.valueOf(a2), "same pos");
        return false;
    }

    private static float b(d dVar, C0575b bVar, float f) {
        float signum = Math.signum(f);
        float abs = Math.abs(f);
        return abs == 1000000.0f ? signum * d.a.i.a.a(dVar, dVar.getType(bVar)) : abs == ((float) a.f8634b) ? dVar.getValue(bVar) * signum : f;
    }

    static boolean b(d dVar, c cVar, h hVar, Number number, long j) {
        int intValue = dVar.getIntValue((C0576c) hVar.f8707b);
        int intValue2 = number.intValue();
        if (d.a.i.a.a(j, 1)) {
            intValue2 += intValue;
        }
        if (hVar.c() || intValue != intValue2) {
            a(hVar.f8707b, Integer.valueOf(intValue), Integer.valueOf(intValue2), (String) null);
            cVar.a(intValue, intValue2);
            return true;
        }
        a(hVar.f8707b, Integer.valueOf(intValue), Integer.valueOf(intValue2), "same pos");
        return false;
    }
}
