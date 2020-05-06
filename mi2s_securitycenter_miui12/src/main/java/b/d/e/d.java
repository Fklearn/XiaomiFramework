package b.d.e;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class d extends e {

    /* renamed from: b  reason: collision with root package name */
    private static final String f2212b = "b.d.e.d";

    /* renamed from: c  reason: collision with root package name */
    private long f2213c = 0;

    private int a(List<String> list, char[] cArr, int i, int i2) {
        while (i2 < list.size()) {
            String str = list.get(i2);
            if (str.length() == i) {
                boolean z = false;
                int i3 = 0;
                while (true) {
                    if (i3 >= i) {
                        z = true;
                        break;
                    } else if (str.charAt(i3) != cArr[i3]) {
                        break;
                    } else {
                        i3++;
                    }
                }
                if (z) {
                    return i2;
                }
            }
            i2++;
        }
        return i2;
    }

    private boolean a(String str, char[] cArr, int i) {
        boolean z;
        for (int i2 = 0; i2 <= str.length() - i; i2++) {
            int i3 = 0;
            while (true) {
                if (i3 >= i) {
                    z = true;
                    break;
                } else if (str.charAt(i2 + i3) != cArr[i3]) {
                    z = false;
                    break;
                } else {
                    i3++;
                }
            }
            if (z) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public g b(Context context) {
        g gVar = new g();
        gVar.a(context);
        return gVar;
    }

    /* access modifiers changed from: protected */
    public boolean b(b bVar, Context context) {
        List<String> list = bVar.g;
        boolean z = false;
        if (list == null || list.size() <= 0) {
            return false;
        }
        String[] strArr = new String[bVar.g.size()];
        for (int i = 0; i < bVar.g.size(); i++) {
            strArr[i] = i.b(bVar.g.get(i));
        }
        try {
            InputStream a2 = c.a(context, "phish");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(a2));
            bufferedReader.readLine();
            boolean z2 = false;
            while (true) {
                if (z2) {
                    break;
                }
                try {
                    String readLine = bufferedReader.readLine();
                    if (readLine == null) {
                        break;
                    }
                    int length = strArr.length;
                    int i2 = 0;
                    while (true) {
                        if (i2 >= length) {
                            break;
                        } else if (strArr[i2].equalsIgnoreCase(readLine)) {
                            z2 = true;
                            break;
                        } else {
                            i2++;
                        }
                    }
                } catch (IOException unused) {
                    z = z2;
                    Log.e(f2212b, "Exception on read phish.");
                    return z;
                }
            }
            bufferedReader.close();
            a2.close();
            return z2;
        } catch (IOException unused2) {
            Log.e(f2212b, "Exception on read phish.");
            return z;
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v3, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v7, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v19, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v6, resolved type: double[][]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v17, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v41, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v42, resolved type: boolean} */
    /* JADX WARNING: Code restructure failed: missing block: B:94:0x0258, code lost:
        if (r4 > 0.0d) goto L_0x0248;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean d(b.d.e.b r30, android.content.Context r31) {
        /*
            r29 = this;
            r0 = r29
            r1 = r30
            r2 = r31
            r29.c(r30, r31)
            r29.a((b.d.e.b) r30, (android.content.Context) r31)
            boolean r3 = b.d.e.e.b(r30)
            r4 = 0
            if (r3 == 0) goto L_0x0014
            return r4
        L_0x0014:
            boolean r3 = b.d.e.e.a((b.d.e.b) r30)
            r5 = 1
            if (r3 == 0) goto L_0x001c
            return r5
        L_0x001c:
            b.d.e.g r3 = r0.b(r2)
            b.d.e.c.e r6 = new b.d.e.c.e
            r6.<init>()
            r6.a((b.d.e.g) r3)
            java.lang.String r7 = "model"
            java.io.InputStream r2 = b.d.e.c.a(r2, r7)
            java.io.DataInputStream r7 = new java.io.DataInputStream
            r7.<init>(r2)
            r7.readInt()
            r7.readInt()
            int r8 = r7.readInt()
            double[] r9 = new double[r8]
            r10 = 2
            double[] r11 = new double[r10]
            r12 = r4
            r13 = r12
        L_0x0044:
            if (r12 >= r8) goto L_0x0232
            r14 = r4
        L_0x0047:
            if (r14 < r10) goto L_0x021e
            int r15 = r7.readInt()
            int[] r14 = new int[]{r10, r15}
            java.lang.Class<double> r5 = double.class
            java.lang.Object r5 = java.lang.reflect.Array.newInstance(r5, r14)
            double[][] r5 = (double[][]) r5
            b.d.e.c.g[] r14 = r6.b()
        L_0x005d:
            int r10 = r14.length
            if (r4 < r10) goto L_0x0207
            r6.c()
            r4 = 0
        L_0x0064:
            if (r4 < r15) goto L_0x01e5
            r6.b(r1)
            int[] r10 = r30.e()
            r4 = 0
        L_0x006e:
            int r14 = r10.length
            if (r4 < r14) goto L_0x01a4
            java.util.List r14 = r30.h()
            int r15 = r14.size()
            int r4 = r7.readInt()
            r5 = 64
            char[] r5 = new char[r5]
            r20 = r6
            r21 = r8
            r6 = 0
            r10 = 0
        L_0x0087:
            r8 = 10
            if (r10 < r4) goto L_0x015c
            int r4 = r7.readInt()
            java.lang.String r6 = r30.c()
            r10 = 0
        L_0x0094:
            if (r10 < r4) goto L_0x012e
            int r4 = b.d.e.c.a.b()
            r5 = 2
            int[] r4 = new int[]{r5, r4}
            java.lang.Class<double> r6 = double.class
            java.lang.Object r4 = java.lang.reflect.Array.newInstance(r6, r4)
            r14 = r4
            double[][] r14 = (double[][]) r14
            r4 = 0
        L_0x00a9:
            int r6 = b.d.e.c.a.b()
            if (r4 < r6) goto L_0x0117
            int r6 = r30.b()
            r4 = 0
        L_0x00b4:
            if (r4 < r5) goto L_0x0109
            r4 = 3
            double[] r4 = new double[r4]
            double r14 = r7.readDouble()
            r6 = 0
            r4[r6] = r14
            double r14 = r7.readDouble()
            r8 = 1
            r4[r8] = r14
            double r14 = r7.readDouble()
            r4[r5] = r14
            r14 = r11[r6]
            r18 = r11[r8]
            double r14 = r14 - r18
            double[] r4 = r0.a((double[]) r4, (b.d.e.b) r1)
            r22 = r4[r8]
            int r5 = (r14 > r22 ? 1 : (r14 == r22 ? 0 : -1))
            if (r5 >= 0) goto L_0x00e3
            r4 = r6
            r1 = 1
        L_0x00df:
            r18 = 1
            goto L_0x0239
        L_0x00e3:
            r22 = r4[r6]
            int r5 = (r14 > r22 ? 1 : (r14 == r22 ? 0 : -1))
            if (r5 <= 0) goto L_0x00ed
            r1 = 1
            r4 = 1
            r6 = 0
            goto L_0x00df
        L_0x00ed:
            r5 = 2
            r22 = r4[r5]
            double r14 = r14 - r22
            r9[r12] = r14
            r4 = r9[r12]
            r14 = 0
            int r4 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r4 <= 0) goto L_0x00fe
            int r13 = r13 + 1
        L_0x00fe:
            int r12 = r12 + 1
            r6 = r20
            r8 = r21
            r4 = 0
            r5 = 1
            r10 = 2
            goto L_0x0044
        L_0x0109:
            r22 = r11[r4]
            r5 = r14[r4]
            r24 = r5[r6]
            double r22 = r22 + r24
            r11[r4] = r22
            int r4 = r4 + 1
            r5 = 2
            goto L_0x00b4
        L_0x0117:
            r5 = 0
            r6 = r14[r5]
            double r22 = r7.readDouble()
            r6[r4] = r22
            r5 = 1
            r6 = r14[r5]
            double r22 = r7.readDouble()
            r6[r4] = r22
            int r4 = r4 + 1
            r5 = 2
            goto L_0x00a9
        L_0x012e:
            r14 = 0
        L_0x012f:
            char r15 = r7.readChar()
            if (r15 != r8) goto L_0x0155
            boolean r22 = r0.a(r6, r5, r14)
            r14 = 0
        L_0x013a:
            r15 = 2
            if (r14 < r15) goto L_0x0141
            int r10 = r10 + 1
            goto L_0x0094
        L_0x0141:
            double r23 = r7.readDouble()
            r25 = r11[r14]
            if (r22 == 0) goto L_0x014a
            goto L_0x014e
        L_0x014a:
            double r23 = b.d.e.i.a((double) r23)
        L_0x014e:
            double r25 = r25 + r23
            r11[r14] = r25
            int r14 = r14 + 1
            goto L_0x013a
        L_0x0155:
            int r22 = r14 + 1
            r5[r14] = r15
            r14 = r22
            goto L_0x012f
        L_0x015c:
            r22 = r4
            r1 = 0
        L_0x015f:
            char r4 = r7.readChar()
            if (r4 != r8) goto L_0x019b
            int r1 = r0.a(r14, r5, r1, r6)
            if (r1 >= r15) goto L_0x016e
            r23 = 1
            goto L_0x0170
        L_0x016e:
            r23 = 0
        L_0x0170:
            if (r23 == 0) goto L_0x0177
            int r1 = r1 + 1
            r24 = r1
            goto L_0x0179
        L_0x0177:
            r24 = r6
        L_0x0179:
            r1 = 0
            r4 = 2
        L_0x017b:
            if (r1 < r4) goto L_0x0187
            int r10 = r10 + 1
            r1 = r30
            r4 = r22
            r6 = r24
            goto L_0x0087
        L_0x0187:
            double r25 = r7.readDouble()
            r27 = r11[r1]
            if (r23 == 0) goto L_0x0190
            goto L_0x0194
        L_0x0190:
            double r25 = b.d.e.i.a((double) r25)
        L_0x0194:
            double r27 = r27 + r25
            r11[r1] = r27
            int r1 = r1 + 1
            goto L_0x017b
        L_0x019b:
            r25 = 2
            int r23 = r1 + 1
            r5[r1] = r4
            r1 = r23
            goto L_0x015f
        L_0x01a4:
            r20 = r6
            r21 = r8
            r23 = 0
            r25 = 2
            r14 = r11[r23]
            r1 = r10[r4]
            if (r1 <= 0) goto L_0x01b7
            r1 = r5[r23]
            r26 = r1[r4]
            goto L_0x01bf
        L_0x01b7:
            r1 = r5[r23]
            r26 = r1[r4]
            double r26 = b.d.e.i.a((double) r26)
        L_0x01bf:
            double r14 = r14 + r26
            r11[r23] = r14
            r1 = 1
            r14 = r11[r1]
            r6 = r10[r4]
            if (r6 <= 0) goto L_0x01cf
            r6 = r5[r1]
            r22 = r6[r4]
            goto L_0x01d7
        L_0x01cf:
            r6 = r5[r1]
            r22 = r6[r4]
            double r22 = b.d.e.i.a((double) r22)
        L_0x01d7:
            double r14 = r14 + r22
            r11[r1] = r14
            int r4 = r4 + 1
            r1 = r30
            r6 = r20
            r8 = r21
            goto L_0x006e
        L_0x01e5:
            r20 = r6
            r21 = r8
            r1 = 1
            r6 = 0
            r25 = 2
            r8 = r5[r6]
            double r18 = r7.readDouble()
            r8[r4] = r18
            r8 = r5[r1]
            double r18 = r7.readDouble()
            r8[r4] = r18
            int r4 = r4 + 1
            r1 = r30
            r6 = r20
            r8 = r21
            goto L_0x0064
        L_0x0207:
            r20 = r6
            r21 = r8
            r1 = 1
            r6 = 0
            r25 = 2
            r8 = r14[r4]
            r8.a(r7)
            int r4 = r4 + 1
            r1 = r30
            r6 = r20
            r8 = r21
            goto L_0x005d
        L_0x021e:
            r1 = r5
            r20 = r6
            r21 = r8
            r25 = r10
            r16 = 0
            r6 = r4
            r11[r14] = r16
            int r14 = r14 + 1
            r6 = r20
            r1 = r30
            goto L_0x0047
        L_0x0232:
            r6 = r4
            r1 = r5
            r21 = r8
            r4 = r6
            r18 = r4
        L_0x0239:
            r3.a()
            r7.close()
            r2.close()
            if (r18 != 0) goto L_0x0263
            int r8 = r21 - r13
            if (r13 <= r8) goto L_0x024a
        L_0x0248:
            r4 = r1
            goto L_0x0263
        L_0x024a:
            if (r13 >= r8) goto L_0x024e
        L_0x024c:
            r4 = r6
            goto L_0x0263
        L_0x024e:
            int r2 = r9.length
            r3 = r6
            r4 = 0
        L_0x0252:
            if (r3 < r2) goto L_0x025b
            r7 = 0
            int r2 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r2 <= 0) goto L_0x024c
            goto L_0x0248
        L_0x025b:
            r7 = 0
            r10 = r9[r3]
            double r4 = r4 + r10
            int r3 = r3 + 1
            goto L_0x0252
        L_0x0263:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: b.d.e.d.d(b.d.e.b, android.content.Context):boolean");
    }
}
