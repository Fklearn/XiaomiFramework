package androidx.recyclerview.widget;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/* renamed from: androidx.recyclerview.widget.o  reason: case insensitive filesystem */
public class C0174o {

    /* renamed from: a  reason: collision with root package name */
    private static final Comparator<e> f1229a = new C0173n();

    /* renamed from: androidx.recyclerview.widget.o$a */
    public static abstract class a {
        public abstract int a();

        public abstract boolean a(int i, int i2);

        public abstract int b();

        public abstract boolean b(int i, int i2);

        @Nullable
        public Object c(int i, int i2) {
            return null;
        }
    }

    /* renamed from: androidx.recyclerview.widget.o$b */
    public static class b {

        /* renamed from: a  reason: collision with root package name */
        private final List<e> f1230a;

        /* renamed from: b  reason: collision with root package name */
        private final int[] f1231b;

        /* renamed from: c  reason: collision with root package name */
        private final int[] f1232c;

        /* renamed from: d  reason: collision with root package name */
        private final a f1233d;
        private final int e;
        private final int f;
        private final boolean g;

        b(a aVar, List<e> list, int[] iArr, int[] iArr2, boolean z) {
            this.f1230a = list;
            this.f1231b = iArr;
            this.f1232c = iArr2;
            Arrays.fill(this.f1231b, 0);
            Arrays.fill(this.f1232c, 0);
            this.f1233d = aVar;
            this.e = aVar.b();
            this.f = aVar.a();
            this.g = z;
            a();
            b();
        }

        private static c a(List<c> list, int i, boolean z) {
            int size = list.size() - 1;
            while (size >= 0) {
                c cVar = list.get(size);
                if (cVar.f1234a == i && cVar.f1236c == z) {
                    list.remove(size);
                    while (size < list.size()) {
                        list.get(size).f1235b += z ? 1 : -1;
                        size++;
                    }
                    return cVar;
                }
                size--;
            }
            return null;
        }

        private void a() {
            e eVar = this.f1230a.isEmpty() ? null : this.f1230a.get(0);
            if (eVar == null || eVar.f1241a != 0 || eVar.f1242b != 0) {
                e eVar2 = new e();
                eVar2.f1241a = 0;
                eVar2.f1242b = 0;
                eVar2.f1244d = false;
                eVar2.f1243c = 0;
                eVar2.e = false;
                this.f1230a.add(0, eVar2);
            }
        }

        private void a(int i, int i2, int i3) {
            if (this.f1231b[i - 1] == 0) {
                a(i, i2, i3, false);
            }
        }

        private void a(List<c> list, x xVar, int i, int i2, int i3) {
            if (!this.g) {
                xVar.a(i, i2);
                return;
            }
            for (int i4 = i2 - 1; i4 >= 0; i4--) {
                int i5 = i3 + i4;
                int i6 = this.f1232c[i5] & 31;
                if (i6 == 0) {
                    xVar.a(i, 1);
                    for (c cVar : list) {
                        cVar.f1235b++;
                    }
                } else if (i6 == 4 || i6 == 8) {
                    int i7 = this.f1232c[i5] >> 5;
                    xVar.c(a(list, i7, true).f1235b, i);
                    if (i6 == 4) {
                        xVar.a(i, 1, this.f1233d.c(i7, i5));
                    }
                } else if (i6 == 16) {
                    list.add(new c(i5, i, false));
                } else {
                    throw new IllegalStateException("unknown flag for pos " + i5 + " " + Long.toBinaryString((long) i6));
                }
            }
        }

        private boolean a(int i, int i2, int i3, boolean z) {
            int i4;
            int i5;
            if (z) {
                i2--;
                i5 = i;
                i4 = i2;
            } else {
                i5 = i - 1;
                i4 = i5;
            }
            while (i3 >= 0) {
                e eVar = this.f1230a.get(i3);
                int i6 = eVar.f1241a;
                int i7 = eVar.f1243c;
                int i8 = i6 + i7;
                int i9 = eVar.f1242b + i7;
                int i10 = 8;
                if (z) {
                    for (int i11 = i5 - 1; i11 >= i8; i11--) {
                        if (this.f1233d.b(i11, i4)) {
                            if (!this.f1233d.a(i11, i4)) {
                                i10 = 4;
                            }
                            this.f1232c[i4] = (i11 << 5) | 16;
                            this.f1231b[i11] = (i4 << 5) | i10;
                            return true;
                        }
                    }
                    continue;
                } else {
                    for (int i12 = i2 - 1; i12 >= i9; i12--) {
                        if (this.f1233d.b(i4, i12)) {
                            if (!this.f1233d.a(i4, i12)) {
                                i10 = 4;
                            }
                            int i13 = i - 1;
                            this.f1231b[i13] = (i12 << 5) | 16;
                            this.f1232c[i12] = (i13 << 5) | i10;
                            return true;
                        }
                    }
                    continue;
                }
                i5 = eVar.f1241a;
                i2 = eVar.f1242b;
                i3--;
            }
            return false;
        }

        private void b() {
            int i = this.e;
            int i2 = this.f;
            for (int size = this.f1230a.size() - 1; size >= 0; size--) {
                e eVar = this.f1230a.get(size);
                int i3 = eVar.f1241a;
                int i4 = eVar.f1243c;
                int i5 = i3 + i4;
                int i6 = eVar.f1242b + i4;
                if (this.g) {
                    while (i > i5) {
                        a(i, i2, size);
                        i--;
                    }
                    while (i2 > i6) {
                        b(i, i2, size);
                        i2--;
                    }
                }
                for (int i7 = 0; i7 < eVar.f1243c; i7++) {
                    int i8 = eVar.f1241a + i7;
                    int i9 = eVar.f1242b + i7;
                    int i10 = this.f1233d.a(i8, i9) ? 1 : 2;
                    this.f1231b[i8] = (i9 << 5) | i10;
                    this.f1232c[i9] = (i8 << 5) | i10;
                }
                i = eVar.f1241a;
                i2 = eVar.f1242b;
            }
        }

        private void b(int i, int i2, int i3) {
            if (this.f1232c[i2 - 1] == 0) {
                a(i, i2, i3, true);
            }
        }

        private void b(List<c> list, x xVar, int i, int i2, int i3) {
            if (!this.g) {
                xVar.b(i, i2);
                return;
            }
            for (int i4 = i2 - 1; i4 >= 0; i4--) {
                int i5 = i3 + i4;
                int i6 = this.f1231b[i5] & 31;
                if (i6 == 0) {
                    xVar.b(i + i4, 1);
                    for (c cVar : list) {
                        cVar.f1235b--;
                    }
                } else if (i6 == 4 || i6 == 8) {
                    int i7 = this.f1231b[i5] >> 5;
                    c a2 = a(list, i7, false);
                    xVar.c(i + i4, a2.f1235b - 1);
                    if (i6 == 4) {
                        xVar.a(a2.f1235b - 1, 1, this.f1233d.c(i5, i7));
                    }
                } else if (i6 == 16) {
                    list.add(new c(i5, i + i4, true));
                } else {
                    throw new IllegalStateException("unknown flag for pos " + i5 + " " + Long.toBinaryString((long) i6));
                }
            }
        }

        public void a(@NonNull RecyclerView.a aVar) {
            a((x) new C0161b(aVar));
        }

        public void a(@NonNull x xVar) {
            C0162c cVar = xVar instanceof C0162c ? (C0162c) xVar : new C0162c(xVar);
            ArrayList arrayList = new ArrayList();
            int i = this.e;
            int i2 = this.f;
            for (int size = this.f1230a.size() - 1; size >= 0; size--) {
                e eVar = this.f1230a.get(size);
                int i3 = eVar.f1243c;
                int i4 = eVar.f1241a + i3;
                int i5 = eVar.f1242b + i3;
                if (i4 < i) {
                    b(arrayList, cVar, i4, i - i4, i4);
                }
                if (i5 < i2) {
                    a(arrayList, cVar, i4, i2 - i5, i5);
                }
                for (int i6 = i3 - 1; i6 >= 0; i6--) {
                    int[] iArr = this.f1231b;
                    int i7 = eVar.f1241a;
                    if ((iArr[i7 + i6] & 31) == 2) {
                        cVar.a(i7 + i6, 1, this.f1233d.c(i7 + i6, eVar.f1242b + i6));
                    }
                }
                i = eVar.f1241a;
                i2 = eVar.f1242b;
            }
            cVar.a();
        }
    }

    /* renamed from: androidx.recyclerview.widget.o$c */
    private static class c {

        /* renamed from: a  reason: collision with root package name */
        int f1234a;

        /* renamed from: b  reason: collision with root package name */
        int f1235b;

        /* renamed from: c  reason: collision with root package name */
        boolean f1236c;

        public c(int i, int i2, boolean z) {
            this.f1234a = i;
            this.f1235b = i2;
            this.f1236c = z;
        }
    }

    /* renamed from: androidx.recyclerview.widget.o$d */
    static class d {

        /* renamed from: a  reason: collision with root package name */
        int f1237a;

        /* renamed from: b  reason: collision with root package name */
        int f1238b;

        /* renamed from: c  reason: collision with root package name */
        int f1239c;

        /* renamed from: d  reason: collision with root package name */
        int f1240d;

        public d() {
        }

        public d(int i, int i2, int i3, int i4) {
            this.f1237a = i;
            this.f1238b = i2;
            this.f1239c = i3;
            this.f1240d = i4;
        }
    }

    /* renamed from: androidx.recyclerview.widget.o$e */
    static class e {

        /* renamed from: a  reason: collision with root package name */
        int f1241a;

        /* renamed from: b  reason: collision with root package name */
        int f1242b;

        /* renamed from: c  reason: collision with root package name */
        int f1243c;

        /* renamed from: d  reason: collision with root package name */
        boolean f1244d;
        boolean e;

        e() {
        }
    }

    @NonNull
    public static b a(@NonNull a aVar) {
        return a(aVar, true);
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x00ae  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00c7  */
    @androidx.annotation.NonNull
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static androidx.recyclerview.widget.C0174o.b a(@androidx.annotation.NonNull androidx.recyclerview.widget.C0174o.a r15, boolean r16) {
        /*
            int r0 = r15.b()
            int r1 = r15.a()
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            androidx.recyclerview.widget.o$d r3 = new androidx.recyclerview.widget.o$d
            r5 = 0
            r3.<init>(r5, r0, r5, r1)
            r2.add(r3)
            int r3 = r0 + r1
            int r0 = r0 - r1
            int r0 = java.lang.Math.abs(r0)
            int r0 = r0 + r3
            int r1 = r0 * 2
            int[] r13 = new int[r1]
            int[] r1 = new int[r1]
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
        L_0x002e:
            boolean r5 = r2.isEmpty()
            if (r5 != 0) goto L_0x00dd
            int r5 = r2.size()
            int r5 = r5 + -1
            java.lang.Object r5 = r2.remove(r5)
            r14 = r5
            androidx.recyclerview.widget.o$d r14 = (androidx.recyclerview.widget.C0174o.d) r14
            int r6 = r14.f1237a
            int r7 = r14.f1238b
            int r8 = r14.f1239c
            int r9 = r14.f1240d
            r5 = r15
            r10 = r13
            r11 = r1
            r12 = r0
            androidx.recyclerview.widget.o$e r5 = a(r5, r6, r7, r8, r9, r10, r11, r12)
            if (r5 == 0) goto L_0x00d8
            int r6 = r5.f1243c
            if (r6 <= 0) goto L_0x005a
            r4.add(r5)
        L_0x005a:
            int r6 = r5.f1241a
            int r7 = r14.f1237a
            int r6 = r6 + r7
            r5.f1241a = r6
            int r6 = r5.f1242b
            int r7 = r14.f1239c
            int r6 = r6 + r7
            r5.f1242b = r6
            boolean r6 = r3.isEmpty()
            if (r6 == 0) goto L_0x0074
            androidx.recyclerview.widget.o$d r6 = new androidx.recyclerview.widget.o$d
            r6.<init>()
            goto L_0x0080
        L_0x0074:
            int r6 = r3.size()
            int r6 = r6 + -1
            java.lang.Object r6 = r3.remove(r6)
            androidx.recyclerview.widget.o$d r6 = (androidx.recyclerview.widget.C0174o.d) r6
        L_0x0080:
            int r7 = r14.f1237a
            r6.f1237a = r7
            int r7 = r14.f1239c
            r6.f1239c = r7
            boolean r7 = r5.e
            if (r7 == 0) goto L_0x0095
            int r7 = r5.f1241a
        L_0x008e:
            r6.f1238b = r7
            int r7 = r5.f1242b
        L_0x0092:
            r6.f1240d = r7
            goto L_0x00a7
        L_0x0095:
            boolean r7 = r5.f1244d
            if (r7 == 0) goto L_0x009e
            int r7 = r5.f1241a
            int r7 = r7 + -1
            goto L_0x008e
        L_0x009e:
            int r7 = r5.f1241a
            r6.f1238b = r7
            int r7 = r5.f1242b
            int r7 = r7 + -1
            goto L_0x0092
        L_0x00a7:
            r2.add(r6)
            boolean r6 = r5.e
            if (r6 == 0) goto L_0x00c7
            boolean r6 = r5.f1244d
            if (r6 == 0) goto L_0x00ba
            int r6 = r5.f1241a
            int r7 = r5.f1243c
            int r6 = r6 + r7
            int r6 = r6 + 1
            goto L_0x00cc
        L_0x00ba:
            int r6 = r5.f1241a
            int r7 = r5.f1243c
            int r6 = r6 + r7
            r14.f1237a = r6
            int r5 = r5.f1242b
            int r5 = r5 + r7
            int r5 = r5 + 1
            goto L_0x00d1
        L_0x00c7:
            int r6 = r5.f1241a
            int r7 = r5.f1243c
            int r6 = r6 + r7
        L_0x00cc:
            r14.f1237a = r6
            int r5 = r5.f1242b
            int r5 = r5 + r7
        L_0x00d1:
            r14.f1239c = r5
            r2.add(r14)
            goto L_0x002e
        L_0x00d8:
            r3.add(r14)
            goto L_0x002e
        L_0x00dd:
            java.util.Comparator<androidx.recyclerview.widget.o$e> r0 = f1229a
            java.util.Collections.sort(r4, r0)
            androidx.recyclerview.widget.o$b r0 = new androidx.recyclerview.widget.o$b
            r2 = r0
            r3 = r15
            r5 = r13
            r6 = r1
            r7 = r16
            r2.<init>(r3, r4, r5, r6, r7)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.recyclerview.widget.C0174o.a(androidx.recyclerview.widget.o$a, boolean):androidx.recyclerview.widget.o$b");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0042, code lost:
        if (r1[r13 - 1] < r1[r13 + r5]) goto L_0x004d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00ba, code lost:
        if (r2[r13 - 1] < r2[r13 + 1]) goto L_0x00c7;
     */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x009c A[LOOP:1: B:10:0x0033->B:33:0x009c, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x0081 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static androidx.recyclerview.widget.C0174o.e a(androidx.recyclerview.widget.C0174o.a r19, int r20, int r21, int r22, int r23, int[] r24, int[] r25, int r26) {
        /*
            r0 = r19
            r1 = r24
            r2 = r25
            int r3 = r21 - r20
            int r4 = r23 - r22
            r5 = 1
            if (r3 < r5) goto L_0x0133
            if (r4 >= r5) goto L_0x0011
            goto L_0x0133
        L_0x0011:
            int r6 = r3 - r4
            int r7 = r3 + r4
            int r7 = r7 + r5
            int r7 = r7 / 2
            int r8 = r26 - r7
            int r8 = r8 - r5
            int r9 = r26 + r7
            int r9 = r9 + r5
            r10 = 0
            java.util.Arrays.fill(r1, r8, r9, r10)
            int r8 = r8 + r6
            int r9 = r9 + r6
            java.util.Arrays.fill(r2, r8, r9, r3)
            int r8 = r6 % 2
            if (r8 == 0) goto L_0x002d
            r8 = r5
            goto L_0x002e
        L_0x002d:
            r8 = r10
        L_0x002e:
            r9 = r10
        L_0x002f:
            if (r9 > r7) goto L_0x012b
            int r11 = -r9
            r12 = r11
        L_0x0033:
            if (r12 > r9) goto L_0x00a2
            if (r12 == r11) goto L_0x004d
            if (r12 == r9) goto L_0x0045
            int r13 = r26 + r12
            int r14 = r13 + -1
            r14 = r1[r14]
            int r13 = r13 + r5
            r13 = r1[r13]
            if (r14 >= r13) goto L_0x0045
            goto L_0x004d
        L_0x0045:
            int r13 = r26 + r12
            int r13 = r13 - r5
            r13 = r1[r13]
            int r13 = r13 + r5
            r14 = r5
            goto L_0x0053
        L_0x004d:
            int r13 = r26 + r12
            int r13 = r13 + r5
            r13 = r1[r13]
            r14 = r10
        L_0x0053:
            int r15 = r13 - r12
        L_0x0055:
            if (r13 >= r3) goto L_0x006a
            if (r15 >= r4) goto L_0x006a
            int r10 = r20 + r13
            int r5 = r22 + r15
            boolean r5 = r0.b(r10, r5)
            if (r5 == 0) goto L_0x006a
            int r13 = r13 + 1
            int r15 = r15 + 1
            r5 = 1
            r10 = 0
            goto L_0x0055
        L_0x006a:
            int r5 = r26 + r12
            r1[r5] = r13
            if (r8 == 0) goto L_0x009c
            int r10 = r6 - r9
            r13 = 1
            int r10 = r10 + r13
            if (r12 < r10) goto L_0x009c
            int r10 = r6 + r9
            int r10 = r10 - r13
            if (r12 > r10) goto L_0x009c
            r10 = r1[r5]
            r13 = r2[r5]
            if (r10 < r13) goto L_0x009c
            androidx.recyclerview.widget.o$e r0 = new androidx.recyclerview.widget.o$e
            r0.<init>()
            r3 = r2[r5]
            r0.f1241a = r3
            int r3 = r0.f1241a
            int r3 = r3 - r12
            r0.f1242b = r3
            r1 = r1[r5]
            r2 = r2[r5]
            int r1 = r1 - r2
            r0.f1243c = r1
            r0.f1244d = r14
            r5 = 0
            r0.e = r5
            return r0
        L_0x009c:
            r5 = 0
            int r12 = r12 + 2
            r10 = r5
            r5 = 1
            goto L_0x0033
        L_0x00a2:
            r5 = r10
            r10 = r11
        L_0x00a4:
            if (r10 > r9) goto L_0x0120
            int r12 = r10 + r6
            int r13 = r9 + r6
            if (r12 == r13) goto L_0x00c6
            int r13 = r11 + r6
            if (r12 == r13) goto L_0x00bd
            int r13 = r26 + r12
            int r14 = r13 + -1
            r14 = r2[r14]
            r15 = 1
            int r13 = r13 + r15
            r13 = r2[r13]
            if (r14 >= r13) goto L_0x00be
            goto L_0x00c7
        L_0x00bd:
            r15 = 1
        L_0x00be:
            int r13 = r26 + r12
            int r13 = r13 + r15
            r13 = r2[r13]
            int r13 = r13 - r15
            r14 = r15
            goto L_0x00cd
        L_0x00c6:
            r15 = 1
        L_0x00c7:
            int r13 = r26 + r12
            int r13 = r13 - r15
            r13 = r2[r13]
            r14 = r5
        L_0x00cd:
            int r16 = r13 - r12
        L_0x00cf:
            if (r13 <= 0) goto L_0x00ec
            if (r16 <= 0) goto L_0x00ec
            int r17 = r20 + r13
            int r5 = r17 + -1
            int r17 = r22 + r16
            r18 = r3
            int r3 = r17 + -1
            boolean r3 = r0.b(r5, r3)
            if (r3 == 0) goto L_0x00ee
            int r13 = r13 + -1
            int r16 = r16 + -1
            r3 = r18
            r5 = 0
            r15 = 1
            goto L_0x00cf
        L_0x00ec:
            r18 = r3
        L_0x00ee:
            int r3 = r26 + r12
            r2[r3] = r13
            if (r8 != 0) goto L_0x0119
            if (r12 < r11) goto L_0x0119
            if (r12 > r9) goto L_0x0119
            r5 = r1[r3]
            r13 = r2[r3]
            if (r5 < r13) goto L_0x0119
            androidx.recyclerview.widget.o$e r0 = new androidx.recyclerview.widget.o$e
            r0.<init>()
            r4 = r2[r3]
            r0.f1241a = r4
            int r4 = r0.f1241a
            int r4 = r4 - r12
            r0.f1242b = r4
            r1 = r1[r3]
            r2 = r2[r3]
            int r1 = r1 - r2
            r0.f1243c = r1
            r0.f1244d = r14
            r3 = 1
            r0.e = r3
            return r0
        L_0x0119:
            r3 = 1
            int r10 = r10 + 2
            r3 = r18
            r5 = 0
            goto L_0x00a4
        L_0x0120:
            r18 = r3
            r3 = 1
            int r9 = r9 + 1
            r5 = r3
            r3 = r18
            r10 = 0
            goto L_0x002f
        L_0x012b:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r1 = "DiffUtil hit an unexpected case while trying to calculate the optimal path. Please make sure your data is not changing during the diff calculation."
            r0.<init>(r1)
            throw r0
        L_0x0133:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.recyclerview.widget.C0174o.a(androidx.recyclerview.widget.o$a, int, int, int, int, int[], int[], int):androidx.recyclerview.widget.o$e");
    }
}
