package b.c.a.c;

import android.opengl.GLES10;
import b.c.a.b.a.e;
import b.c.a.b.a.i;
import b.c.a.b.e.a;

public final class b {

    /* renamed from: a  reason: collision with root package name */
    private static e f2071a;

    static {
        int[] iArr = new int[1];
        GLES10.glGetIntegerv(3379, iArr, 0);
        int max = Math.max(iArr[0], 2048);
        f2071a = new e(max, max);
    }

    private static int a(int i, int i2, int i3, boolean z) {
        int b2 = f2071a.b();
        int a2 = f2071a.a();
        while (true) {
            if (i / i3 <= b2 && i2 / i3 <= a2) {
                return i3;
            }
            i3 = z ? i3 * 2 : i3 + 1;
        }
    }

    public static int a(e eVar) {
        int b2 = eVar.b();
        int a2 = eVar.a();
        return Math.max((int) Math.ceil((double) (((float) b2) / ((float) f2071a.b()))), (int) Math.ceil((double) (((float) a2) / ((float) f2071a.a()))));
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x0059  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int a(b.c.a.b.a.e r6, b.c.a.b.a.e r7, b.c.a.b.a.i r8, boolean r9) {
        /*
            int r0 = r6.b()
            int r6 = r6.a()
            int r1 = r7.b()
            int r7 = r7.a()
            int[] r2 = b.c.a.c.a.f2070a
            int r8 = r8.ordinal()
            r8 = r2[r8]
            r2 = 1
            if (r8 == r2) goto L_0x003d
            r3 = 2
            if (r8 == r3) goto L_0x0020
            r7 = r2
            goto L_0x0057
        L_0x0020:
            if (r9 == 0) goto L_0x0034
            int r8 = r0 / 2
            int r3 = r6 / 2
            r4 = r2
        L_0x0027:
            int r5 = r8 / r4
            if (r5 <= r1) goto L_0x0032
            int r5 = r3 / r4
            if (r5 <= r7) goto L_0x0032
            int r4 = r4 * 2
            goto L_0x0027
        L_0x0032:
            r7 = r4
            goto L_0x0057
        L_0x0034:
            int r8 = r0 / r1
            int r7 = r6 / r7
            int r7 = java.lang.Math.min(r8, r7)
            goto L_0x0057
        L_0x003d:
            if (r9 == 0) goto L_0x004f
            int r8 = r0 / 2
            int r3 = r6 / 2
            r4 = r2
        L_0x0044:
            int r5 = r8 / r4
            if (r5 > r1) goto L_0x004c
            int r5 = r3 / r4
            if (r5 <= r7) goto L_0x0032
        L_0x004c:
            int r4 = r4 * 2
            goto L_0x0044
        L_0x004f:
            int r8 = r0 / r1
            int r7 = r6 / r7
            int r7 = java.lang.Math.max(r8, r7)
        L_0x0057:
            if (r7 >= r2) goto L_0x005a
            r7 = r2
        L_0x005a:
            int r6 = a((int) r0, (int) r6, (int) r7, (boolean) r9)
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: b.c.a.c.b.a(b.c.a.b.a.e, b.c.a.b.a.e, b.c.a.b.a.i, boolean):int");
    }

    public static e a(a aVar, e eVar) {
        int c2 = aVar.c();
        if (c2 <= 0) {
            c2 = eVar.b();
        }
        int a2 = aVar.a();
        if (a2 <= 0) {
            a2 = eVar.a();
        }
        return new e(c2, a2);
    }

    public static float b(e eVar, e eVar2, i iVar, boolean z) {
        int b2 = eVar.b();
        int a2 = eVar.a();
        int b3 = eVar2.b();
        int a3 = eVar2.a();
        float f = (float) b2;
        float f2 = f / ((float) b3);
        float f3 = (float) a2;
        float f4 = f3 / ((float) a3);
        if ((iVar != i.FIT_INSIDE || f2 < f4) && (iVar != i.CROP || f2 >= f4)) {
            b3 = (int) (f / f4);
        } else {
            a3 = (int) (f3 / f2);
        }
        if ((z || b3 >= b2 || a3 >= a2) && (!z || b3 == b2 || a3 == a2)) {
            return 1.0f;
        }
        return ((float) b3) / f;
    }
}
