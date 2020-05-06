package androidx.recyclerview.widget;

import androidx.recyclerview.widget.C0160a;
import java.util.List;

class y {

    /* renamed from: a  reason: collision with root package name */
    final a f1270a;

    interface a {
        C0160a.b a(int i, int i2, int i3, Object obj);

        void a(C0160a.b bVar);
    }

    y(a aVar) {
        this.f1270a = aVar;
    }

    private void a(List<C0160a.b> list, int i, int i2) {
        C0160a.b bVar = list.get(i);
        C0160a.b bVar2 = list.get(i2);
        int i3 = bVar2.f1181a;
        if (i3 == 1) {
            c(list, i, bVar, i2, bVar2);
        } else if (i3 == 2) {
            a(list, i, bVar, i2, bVar2);
        } else if (i3 == 4) {
            b(list, i, bVar, i2, bVar2);
        }
    }

    private int b(List<C0160a.b> list) {
        boolean z = false;
        for (int size = list.size() - 1; size >= 0; size--) {
            if (list.get(size).f1181a != 8) {
                z = true;
            } else if (z) {
                return size;
            }
        }
        return -1;
    }

    private void c(List<C0160a.b> list, int i, C0160a.b bVar, int i2, C0160a.b bVar2) {
        int i3 = bVar.f1184d < bVar2.f1182b ? -1 : 0;
        if (bVar.f1182b < bVar2.f1182b) {
            i3++;
        }
        int i4 = bVar2.f1182b;
        int i5 = bVar.f1182b;
        if (i4 <= i5) {
            bVar.f1182b = i5 + bVar2.f1184d;
        }
        int i6 = bVar2.f1182b;
        int i7 = bVar.f1184d;
        if (i6 <= i7) {
            bVar.f1184d = i7 + bVar2.f1184d;
        }
        bVar2.f1182b += i3;
        list.set(i, bVar2);
        list.set(i2, bVar);
    }

    /* access modifiers changed from: package-private */
    public void a(List<C0160a.b> list) {
        while (true) {
            int b2 = b(list);
            if (b2 != -1) {
                a(list, b2, b2 + 1);
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00a1, code lost:
        if (r0 > r14.f1182b) goto L_0x00cd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00cb, code lost:
        if (r0 >= r14.f1182b) goto L_0x00cd;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(java.util.List<androidx.recyclerview.widget.C0160a.b> r10, int r11, androidx.recyclerview.widget.C0160a.b r12, int r13, androidx.recyclerview.widget.C0160a.b r14) {
        /*
            r9 = this;
            int r0 = r12.f1182b
            int r1 = r12.f1184d
            r2 = 0
            r3 = 1
            if (r0 >= r1) goto L_0x0016
            int r4 = r14.f1182b
            if (r4 != r0) goto L_0x0014
            int r4 = r14.f1184d
            int r1 = r1 - r0
            if (r4 != r1) goto L_0x0014
            r0 = r2
            r2 = r3
            goto L_0x0025
        L_0x0014:
            r0 = r2
            goto L_0x0025
        L_0x0016:
            int r4 = r14.f1182b
            int r5 = r1 + 1
            if (r4 != r5) goto L_0x0024
            int r4 = r14.f1184d
            int r0 = r0 - r1
            if (r4 != r0) goto L_0x0024
            r0 = r3
            r2 = r0
            goto L_0x0025
        L_0x0024:
            r0 = r3
        L_0x0025:
            int r1 = r12.f1184d
            int r4 = r14.f1182b
            r5 = 2
            if (r1 >= r4) goto L_0x0030
            int r4 = r4 - r3
            r14.f1182b = r4
            goto L_0x0049
        L_0x0030:
            int r6 = r14.f1184d
            int r4 = r4 + r6
            if (r1 >= r4) goto L_0x0049
            int r6 = r6 - r3
            r14.f1184d = r6
            r12.f1181a = r5
            r12.f1184d = r3
            int r11 = r14.f1184d
            if (r11 != 0) goto L_0x0048
            r10.remove(r13)
            androidx.recyclerview.widget.y$a r10 = r9.f1270a
            r10.a(r14)
        L_0x0048:
            return
        L_0x0049:
            int r1 = r12.f1182b
            int r4 = r14.f1182b
            r6 = 0
            if (r1 > r4) goto L_0x0054
            int r4 = r4 + r3
            r14.f1182b = r4
            goto L_0x006a
        L_0x0054:
            int r7 = r14.f1184d
            int r8 = r4 + r7
            if (r1 >= r8) goto L_0x006a
            int r4 = r4 + r7
            int r4 = r4 - r1
            androidx.recyclerview.widget.y$a r7 = r9.f1270a
            int r1 = r1 + r3
            androidx.recyclerview.widget.a$b r6 = r7.a(r5, r1, r4, r6)
            int r1 = r12.f1182b
            int r3 = r14.f1182b
            int r1 = r1 - r3
            r14.f1184d = r1
        L_0x006a:
            if (r2 == 0) goto L_0x0078
            r10.set(r11, r14)
            r10.remove(r13)
            androidx.recyclerview.widget.y$a r10 = r9.f1270a
            r10.a(r12)
            return
        L_0x0078:
            if (r0 == 0) goto L_0x00a4
            if (r6 == 0) goto L_0x0092
            int r0 = r12.f1182b
            int r1 = r6.f1182b
            if (r0 <= r1) goto L_0x0087
            int r1 = r6.f1184d
            int r0 = r0 - r1
            r12.f1182b = r0
        L_0x0087:
            int r0 = r12.f1184d
            int r1 = r6.f1182b
            if (r0 <= r1) goto L_0x0092
            int r1 = r6.f1184d
            int r0 = r0 - r1
            r12.f1184d = r0
        L_0x0092:
            int r0 = r12.f1182b
            int r1 = r14.f1182b
            if (r0 <= r1) goto L_0x009d
            int r1 = r14.f1184d
            int r0 = r0 - r1
            r12.f1182b = r0
        L_0x009d:
            int r0 = r12.f1184d
            int r1 = r14.f1182b
            if (r0 <= r1) goto L_0x00d2
            goto L_0x00cd
        L_0x00a4:
            if (r6 == 0) goto L_0x00bc
            int r0 = r12.f1182b
            int r1 = r6.f1182b
            if (r0 < r1) goto L_0x00b1
            int r1 = r6.f1184d
            int r0 = r0 - r1
            r12.f1182b = r0
        L_0x00b1:
            int r0 = r12.f1184d
            int r1 = r6.f1182b
            if (r0 < r1) goto L_0x00bc
            int r1 = r6.f1184d
            int r0 = r0 - r1
            r12.f1184d = r0
        L_0x00bc:
            int r0 = r12.f1182b
            int r1 = r14.f1182b
            if (r0 < r1) goto L_0x00c7
            int r1 = r14.f1184d
            int r0 = r0 - r1
            r12.f1182b = r0
        L_0x00c7:
            int r0 = r12.f1184d
            int r1 = r14.f1182b
            if (r0 < r1) goto L_0x00d2
        L_0x00cd:
            int r1 = r14.f1184d
            int r0 = r0 - r1
            r12.f1184d = r0
        L_0x00d2:
            r10.set(r11, r14)
            int r14 = r12.f1182b
            int r0 = r12.f1184d
            if (r14 == r0) goto L_0x00df
            r10.set(r13, r12)
            goto L_0x00e2
        L_0x00df:
            r10.remove(r13)
        L_0x00e2:
            if (r6 == 0) goto L_0x00e7
            r10.add(r11, r6)
        L_0x00e7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.recyclerview.widget.y.a(java.util.List, int, androidx.recyclerview.widget.a$b, int, androidx.recyclerview.widget.a$b):void");
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:10:0x002b  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0048  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x004c  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0056  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x005b  */
    /* JADX WARNING: Removed duplicated region for block: B:22:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x0027  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void b(java.util.List<androidx.recyclerview.widget.C0160a.b> r9, int r10, androidx.recyclerview.widget.C0160a.b r11, int r12, androidx.recyclerview.widget.C0160a.b r13) {
        /*
            r8 = this;
            int r0 = r11.f1184d
            int r1 = r13.f1182b
            r2 = 4
            r3 = 0
            r4 = 1
            if (r0 >= r1) goto L_0x000d
            int r1 = r1 - r4
            r13.f1182b = r1
            goto L_0x0020
        L_0x000d:
            int r5 = r13.f1184d
            int r1 = r1 + r5
            if (r0 >= r1) goto L_0x0020
            int r5 = r5 - r4
            r13.f1184d = r5
            androidx.recyclerview.widget.y$a r0 = r8.f1270a
            int r1 = r11.f1182b
            java.lang.Object r5 = r13.f1183c
            androidx.recyclerview.widget.a$b r0 = r0.a(r2, r1, r4, r5)
            goto L_0x0021
        L_0x0020:
            r0 = r3
        L_0x0021:
            int r1 = r11.f1182b
            int r5 = r13.f1182b
            if (r1 > r5) goto L_0x002b
            int r5 = r5 + r4
            r13.f1182b = r5
            goto L_0x0041
        L_0x002b:
            int r6 = r13.f1184d
            int r7 = r5 + r6
            if (r1 >= r7) goto L_0x0041
            int r5 = r5 + r6
            int r5 = r5 - r1
            androidx.recyclerview.widget.y$a r3 = r8.f1270a
            int r1 = r1 + r4
            java.lang.Object r4 = r13.f1183c
            androidx.recyclerview.widget.a$b r3 = r3.a(r2, r1, r5, r4)
            int r1 = r13.f1184d
            int r1 = r1 - r5
            r13.f1184d = r1
        L_0x0041:
            r9.set(r12, r11)
            int r11 = r13.f1184d
            if (r11 <= 0) goto L_0x004c
            r9.set(r10, r13)
            goto L_0x0054
        L_0x004c:
            r9.remove(r10)
            androidx.recyclerview.widget.y$a r11 = r8.f1270a
            r11.a(r13)
        L_0x0054:
            if (r0 == 0) goto L_0x0059
            r9.add(r10, r0)
        L_0x0059:
            if (r3 == 0) goto L_0x005e
            r9.add(r10, r3)
        L_0x005e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.recyclerview.widget.y.b(java.util.List, int, androidx.recyclerview.widget.a$b, int, androidx.recyclerview.widget.a$b):void");
    }
}
