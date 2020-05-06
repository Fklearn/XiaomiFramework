package b.d.a.a.d.a;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class b implements Comparable<b> {

    /* renamed from: a  reason: collision with root package name */
    private static final Map<Character, Character> f2113a = new HashMap(16, 0.95f);

    /* renamed from: b  reason: collision with root package name */
    private b[] f2114b;

    /* renamed from: c  reason: collision with root package name */
    private Character f2115c;

    /* renamed from: d  reason: collision with root package name */
    private int f2116d = 0;
    private int e = 0;

    b(Character ch) {
        if (ch != null) {
            this.f2115c = ch;
            return;
        }
        throw new IllegalArgumentException("参数为空异常，字符不能为空");
    }

    private b a(Character ch, int i) {
        b[] a2 = a();
        b bVar = new b(ch);
        int binarySearch = Arrays.binarySearch(a2, 0, this.f2116d, bVar);
        b bVar2 = binarySearch >= 0 ? a2[binarySearch] : null;
        if (bVar2 != null || i != 1) {
            return bVar2;
        }
        for (b bVar3 : a2) {
            if (bVar3 == null) {
                System.out.println("----");
            }
        }
        int i2 = this.f2116d;
        if (i2 < a2.length) {
            a2[i2] = bVar;
        } else {
            a2 = a(i2 + 1);
            a2[this.f2116d] = bVar;
        }
        this.f2116d++;
        Arrays.sort(a2, 0, this.f2116d);
        return bVar;
    }

    private c a(char[] cArr, int i, int i2, c cVar) {
        int binarySearch;
        if (cVar == null) {
            cVar = new c();
            cVar.a(i);
        } else {
            cVar.e();
        }
        cVar.b(i);
        Character valueOf = Character.valueOf(cArr[i]);
        b bVar = null;
        b[] bVarArr = this.f2114b;
        if (bVarArr != null && (binarySearch = Arrays.binarySearch(bVarArr, 0, this.f2116d, new b(valueOf))) >= 0) {
            bVar = bVarArr[binarySearch];
        }
        if (bVar != null) {
            if (i2 > 1) {
                return bVar.a(cArr, i + 1, i2 - 1, cVar);
            }
            if (i2 == 1) {
                if (bVar.e == 1) {
                    cVar.c();
                }
                if (bVar.b()) {
                    cVar.d();
                    cVar.a(bVar);
                }
            }
        }
        return cVar;
    }

    private synchronized void a(char[] cArr, int i, int i2, int i3) {
        Character valueOf = Character.valueOf(cArr[i]);
        Character ch = f2113a.get(valueOf);
        if (ch == null) {
            f2113a.put(valueOf, valueOf);
        } else {
            valueOf = ch;
        }
        b a2 = a(valueOf, i3);
        if (a2 != null) {
            if (i2 > 1) {
                a2.a(cArr, i + 1, i2 - 1, i3);
            } else if (i2 == 1) {
                a2.e = i3;
            }
        }
    }

    private b[] a() {
        if (this.f2114b == null) {
            synchronized (this) {
                if (this.f2114b == null) {
                    this.f2114b = new b[0];
                }
            }
        }
        return this.f2114b;
    }

    private b[] a(int i) {
        synchronized (this) {
            b[] bVarArr = new b[i];
            System.arraycopy(this.f2114b, 0, bVarArr, 0, this.f2114b.length);
            this.f2114b = bVarArr;
        }
        return this.f2114b;
    }

    private boolean b() {
        return this.f2116d > 0;
    }

    /* renamed from: a */
    public int compareTo(b bVar) {
        return this.f2115c.compareTo(bVar.f2115c);
    }

    /* access modifiers changed from: package-private */
    public c a(char[] cArr, int i, int i2) {
        return a(cArr, i, i2, (c) null);
    }

    /* access modifiers changed from: package-private */
    public void a(char[] cArr) {
        a(cArr, 0, cArr.length, 1);
    }
}
