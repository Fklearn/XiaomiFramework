package androidx.recyclerview.widget;

import a.d.e.d;
import a.d.e.e;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.y;
import java.util.ArrayList;
import java.util.List;

/* renamed from: androidx.recyclerview.widget.a  reason: case insensitive filesystem */
class C0160a implements y.a {

    /* renamed from: a  reason: collision with root package name */
    private d<b> f1177a;

    /* renamed from: b  reason: collision with root package name */
    final ArrayList<b> f1178b;

    /* renamed from: c  reason: collision with root package name */
    final ArrayList<b> f1179c;

    /* renamed from: d  reason: collision with root package name */
    final C0018a f1180d;
    Runnable e;
    final boolean f;
    final y g;
    private int h;

    /* renamed from: androidx.recyclerview.widget.a$a  reason: collision with other inner class name */
    interface C0018a {
        RecyclerView.u a(int i);

        void a(int i, int i2);

        void a(int i, int i2, Object obj);

        void a(b bVar);

        void b(int i, int i2);

        void b(b bVar);

        void c(int i, int i2);

        void d(int i, int i2);
    }

    /* renamed from: androidx.recyclerview.widget.a$b */
    static class b {

        /* renamed from: a  reason: collision with root package name */
        int f1181a;

        /* renamed from: b  reason: collision with root package name */
        int f1182b;

        /* renamed from: c  reason: collision with root package name */
        Object f1183c;

        /* renamed from: d  reason: collision with root package name */
        int f1184d;

        b(int i, int i2, int i3, Object obj) {
            this.f1181a = i;
            this.f1182b = i2;
            this.f1184d = i3;
            this.f1183c = obj;
        }

        /* access modifiers changed from: package-private */
        public String a() {
            int i = this.f1181a;
            return i != 1 ? i != 2 ? i != 4 ? i != 8 ? "??" : "mv" : "up" : "rm" : "add";
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || b.class != obj.getClass()) {
                return false;
            }
            b bVar = (b) obj;
            int i = this.f1181a;
            if (i != bVar.f1181a) {
                return false;
            }
            if (i == 8 && Math.abs(this.f1184d - this.f1182b) == 1 && this.f1184d == bVar.f1182b && this.f1182b == bVar.f1184d) {
                return true;
            }
            if (this.f1184d != bVar.f1184d || this.f1182b != bVar.f1182b) {
                return false;
            }
            Object obj2 = this.f1183c;
            if (obj2 != null) {
                if (!obj2.equals(bVar.f1183c)) {
                    return false;
                }
            } else if (bVar.f1183c != null) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return (((this.f1181a * 31) + this.f1182b) * 31) + this.f1184d;
        }

        public String toString() {
            return Integer.toHexString(System.identityHashCode(this)) + "[" + a() + ",s:" + this.f1182b + "c:" + this.f1184d + ",p:" + this.f1183c + "]";
        }
    }

    C0160a(C0018a aVar) {
        this(aVar, false);
    }

    C0160a(C0018a aVar, boolean z) {
        this.f1177a = new e(30);
        this.f1178b = new ArrayList<>();
        this.f1179c = new ArrayList<>();
        this.h = 0;
        this.f1180d = aVar;
        this.f = z;
        this.g = new y(this);
    }

    private void b(b bVar) {
        g(bVar);
    }

    private void c(b bVar) {
        g(bVar);
    }

    private int d(int i, int i2) {
        int i3;
        int i4;
        int i5;
        int i6;
        for (int size = this.f1179c.size() - 1; size >= 0; size--) {
            b bVar = this.f1179c.get(size);
            int i7 = bVar.f1181a;
            if (i7 == 8) {
                int i8 = bVar.f1182b;
                int i9 = bVar.f1184d;
                if (i8 >= i9) {
                    int i10 = i9;
                    i9 = i8;
                    i8 = i10;
                }
                if (i < i8 || i > i9) {
                    int i11 = bVar.f1182b;
                    if (i < i11) {
                        if (i2 == 1) {
                            bVar.f1182b = i11 + 1;
                            i4 = bVar.f1184d + 1;
                        } else if (i2 == 2) {
                            bVar.f1182b = i11 - 1;
                            i4 = bVar.f1184d - 1;
                        }
                        bVar.f1184d = i4;
                    }
                } else {
                    int i12 = bVar.f1182b;
                    if (i8 == i12) {
                        if (i2 == 1) {
                            i6 = bVar.f1184d + 1;
                        } else {
                            if (i2 == 2) {
                                i6 = bVar.f1184d - 1;
                            }
                            i++;
                        }
                        bVar.f1184d = i6;
                        i++;
                    } else {
                        if (i2 == 1) {
                            i5 = i12 + 1;
                        } else {
                            if (i2 == 2) {
                                i5 = i12 - 1;
                            }
                            i--;
                        }
                        bVar.f1182b = i5;
                        i--;
                    }
                }
            } else {
                int i13 = bVar.f1182b;
                if (i13 > i) {
                    if (i2 == 1) {
                        i3 = i13 + 1;
                    } else if (i2 == 2) {
                        i3 = i13 - 1;
                    }
                    bVar.f1182b = i3;
                } else if (i7 == 1) {
                    i -= bVar.f1184d;
                } else if (i7 == 2) {
                    i += bVar.f1184d;
                }
            }
        }
        for (int size2 = this.f1179c.size() - 1; size2 >= 0; size2--) {
            b bVar2 = this.f1179c.get(size2);
            if (bVar2.f1181a == 8) {
                int i14 = bVar2.f1184d;
                if (i14 != bVar2.f1182b && i14 >= 0) {
                }
            } else if (bVar2.f1184d > 0) {
            }
            this.f1179c.remove(size2);
            a(bVar2);
        }
        return i;
    }

    private void d(b bVar) {
        boolean z;
        boolean z2;
        boolean z3;
        int i = bVar.f1182b;
        int i2 = 0;
        boolean z4 = true;
        int i3 = bVar.f1184d + i;
        int i4 = i;
        while (i4 < i3) {
            if (this.f1180d.a(i4) != null || d(i4)) {
                if (!z4) {
                    f(a(2, i, i2, (Object) null));
                    z3 = true;
                } else {
                    z3 = false;
                }
                z = true;
            } else {
                if (z4) {
                    g(a(2, i, i2, (Object) null));
                    z2 = true;
                } else {
                    z2 = false;
                }
                z = false;
            }
            if (z2) {
                i4 -= i2;
                i3 -= i2;
                i2 = 1;
            } else {
                i2++;
            }
            i4++;
            z4 = z;
        }
        if (i2 != bVar.f1184d) {
            a(bVar);
            bVar = a(2, i, i2, (Object) null);
        }
        if (!z4) {
            f(bVar);
        } else {
            g(bVar);
        }
    }

    private boolean d(int i) {
        int size = this.f1179c.size();
        for (int i2 = 0; i2 < size; i2++) {
            b bVar = this.f1179c.get(i2);
            int i3 = bVar.f1181a;
            if (i3 == 8) {
                if (a(bVar.f1184d, i2 + 1) == i) {
                    return true;
                }
            } else if (i3 == 1) {
                int i4 = bVar.f1182b;
                int i5 = bVar.f1184d + i4;
                while (i4 < i5) {
                    if (a(i4, i2 + 1) == i) {
                        return true;
                    }
                    i4++;
                }
                continue;
            } else {
                continue;
            }
        }
        return false;
    }

    private void e(b bVar) {
        int i = bVar.f1182b;
        int i2 = bVar.f1184d + i;
        int i3 = i;
        boolean z = true;
        int i4 = 0;
        while (i < i2) {
            if (this.f1180d.a(i) != null || d(i)) {
                if (!z) {
                    f(a(4, i3, i4, bVar.f1183c));
                    i3 = i;
                    i4 = 0;
                }
                z = true;
            } else {
                if (z) {
                    g(a(4, i3, i4, bVar.f1183c));
                    i3 = i;
                    i4 = 0;
                }
                z = false;
            }
            i4++;
            i++;
        }
        if (i4 != bVar.f1184d) {
            Object obj = bVar.f1183c;
            a(bVar);
            bVar = a(4, i3, i4, obj);
        }
        if (!z) {
            f(bVar);
        } else {
            g(bVar);
        }
    }

    private void f(b bVar) {
        int i;
        int i2 = bVar.f1181a;
        if (i2 == 1 || i2 == 8) {
            throw new IllegalArgumentException("should not dispatch add or move for pre layout");
        }
        int d2 = d(bVar.f1182b, i2);
        int i3 = bVar.f1182b;
        int i4 = bVar.f1181a;
        if (i4 == 2) {
            i = 0;
        } else if (i4 == 4) {
            i = 1;
        } else {
            throw new IllegalArgumentException("op should be remove or update." + bVar);
        }
        int i5 = d2;
        int i6 = i3;
        int i7 = 1;
        for (int i8 = 1; i8 < bVar.f1184d; i8++) {
            int d3 = d(bVar.f1182b + (i * i8), bVar.f1181a);
            int i9 = bVar.f1181a;
            if (i9 == 2 ? d3 == i5 : i9 == 4 && d3 == i5 + 1) {
                i7++;
            } else {
                b a2 = a(bVar.f1181a, i5, i7, bVar.f1183c);
                a(a2, i6);
                a(a2);
                if (bVar.f1181a == 4) {
                    i6 += i7;
                }
                i7 = 1;
                i5 = d3;
            }
        }
        Object obj = bVar.f1183c;
        a(bVar);
        if (i7 > 0) {
            b a3 = a(bVar.f1181a, i5, i7, obj);
            a(a3, i6);
            a(a3);
        }
    }

    private void g(b bVar) {
        this.f1179c.add(bVar);
        int i = bVar.f1181a;
        if (i == 1) {
            this.f1180d.c(bVar.f1182b, bVar.f1184d);
        } else if (i == 2) {
            this.f1180d.b(bVar.f1182b, bVar.f1184d);
        } else if (i == 4) {
            this.f1180d.a(bVar.f1182b, bVar.f1184d, bVar.f1183c);
        } else if (i == 8) {
            this.f1180d.a(bVar.f1182b, bVar.f1184d);
        } else {
            throw new IllegalArgumentException("Unknown update op type for " + bVar);
        }
    }

    public int a(int i) {
        int size = this.f1178b.size();
        for (int i2 = 0; i2 < size; i2++) {
            b bVar = this.f1178b.get(i2);
            int i3 = bVar.f1181a;
            if (i3 != 1) {
                if (i3 == 2) {
                    int i4 = bVar.f1182b;
                    if (i4 <= i) {
                        int i5 = bVar.f1184d;
                        if (i4 + i5 > i) {
                            return -1;
                        }
                        i -= i5;
                    } else {
                        continue;
                    }
                } else if (i3 == 8) {
                    int i6 = bVar.f1182b;
                    if (i6 == i) {
                        i = bVar.f1184d;
                    } else {
                        if (i6 < i) {
                            i--;
                        }
                        if (bVar.f1184d <= i) {
                            i++;
                        }
                    }
                }
            } else if (bVar.f1182b <= i) {
                i += bVar.f1184d;
            }
        }
        return i;
    }

    /* access modifiers changed from: package-private */
    public int a(int i, int i2) {
        int size = this.f1179c.size();
        while (i2 < size) {
            b bVar = this.f1179c.get(i2);
            int i3 = bVar.f1181a;
            if (i3 == 8) {
                int i4 = bVar.f1182b;
                if (i4 == i) {
                    i = bVar.f1184d;
                } else {
                    if (i4 < i) {
                        i--;
                    }
                    if (bVar.f1184d <= i) {
                        i++;
                    }
                }
            } else {
                int i5 = bVar.f1182b;
                if (i5 > i) {
                    continue;
                } else if (i3 == 2) {
                    int i6 = bVar.f1184d;
                    if (i < i5 + i6) {
                        return -1;
                    }
                    i -= i6;
                } else if (i3 == 1) {
                    i += bVar.f1184d;
                }
            }
            i2++;
        }
        return i;
    }

    public b a(int i, int i2, int i3, Object obj) {
        b acquire = this.f1177a.acquire();
        if (acquire == null) {
            return new b(i, i2, i3, obj);
        }
        acquire.f1181a = i;
        acquire.f1182b = i2;
        acquire.f1184d = i3;
        acquire.f1183c = obj;
        return acquire;
    }

    /* access modifiers changed from: package-private */
    public void a() {
        int size = this.f1179c.size();
        for (int i = 0; i < size; i++) {
            this.f1180d.b(this.f1179c.get(i));
        }
        a((List<b>) this.f1179c);
        this.h = 0;
    }

    public void a(b bVar) {
        if (!this.f) {
            bVar.f1183c = null;
            this.f1177a.release(bVar);
        }
    }

    /* access modifiers changed from: package-private */
    public void a(b bVar, int i) {
        this.f1180d.a(bVar);
        int i2 = bVar.f1181a;
        if (i2 == 2) {
            this.f1180d.d(i, bVar.f1184d);
        } else if (i2 == 4) {
            this.f1180d.a(i, bVar.f1184d, bVar.f1183c);
        } else {
            throw new IllegalArgumentException("only remove and update ops can be dispatched in first pass");
        }
    }

    /* access modifiers changed from: package-private */
    public void a(List<b> list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            a(list.get(i));
        }
        list.clear();
    }

    /* access modifiers changed from: package-private */
    public boolean a(int i, int i2, int i3) {
        if (i == i2) {
            return false;
        }
        if (i3 == 1) {
            this.f1178b.add(a(8, i, i2, (Object) null));
            this.h |= 8;
            return this.f1178b.size() == 1;
        }
        throw new IllegalArgumentException("Moving more than 1 item is not supported yet");
    }

    /* access modifiers changed from: package-private */
    public boolean a(int i, int i2, Object obj) {
        if (i2 < 1) {
            return false;
        }
        this.f1178b.add(a(4, i, i2, obj));
        this.h |= 4;
        return this.f1178b.size() == 1;
    }

    /* access modifiers changed from: package-private */
    public int b(int i) {
        return a(i, 0);
    }

    /* access modifiers changed from: package-private */
    public void b() {
        a();
        int size = this.f1178b.size();
        for (int i = 0; i < size; i++) {
            b bVar = this.f1178b.get(i);
            int i2 = bVar.f1181a;
            if (i2 == 1) {
                this.f1180d.b(bVar);
                this.f1180d.c(bVar.f1182b, bVar.f1184d);
            } else if (i2 == 2) {
                this.f1180d.b(bVar);
                this.f1180d.d(bVar.f1182b, bVar.f1184d);
            } else if (i2 == 4) {
                this.f1180d.b(bVar);
                this.f1180d.a(bVar.f1182b, bVar.f1184d, bVar.f1183c);
            } else if (i2 == 8) {
                this.f1180d.b(bVar);
                this.f1180d.a(bVar.f1182b, bVar.f1184d);
            }
            Runnable runnable = this.e;
            if (runnable != null) {
                runnable.run();
            }
        }
        a((List<b>) this.f1178b);
        this.h = 0;
    }

    /* access modifiers changed from: package-private */
    public boolean b(int i, int i2) {
        if (i2 < 1) {
            return false;
        }
        this.f1178b.add(a(1, i, i2, (Object) null));
        this.h |= 1;
        return this.f1178b.size() == 1;
    }

    /* access modifiers changed from: package-private */
    public boolean c() {
        return this.f1178b.size() > 0;
    }

    /* access modifiers changed from: package-private */
    public boolean c(int i) {
        return (i & this.h) != 0;
    }

    /* access modifiers changed from: package-private */
    public boolean c(int i, int i2) {
        if (i2 < 1) {
            return false;
        }
        this.f1178b.add(a(2, i, i2, (Object) null));
        this.h |= 2;
        return this.f1178b.size() == 1;
    }

    /* access modifiers changed from: package-private */
    public boolean d() {
        return !this.f1179c.isEmpty() && !this.f1178b.isEmpty();
    }

    /* access modifiers changed from: package-private */
    public void e() {
        this.g.a(this.f1178b);
        int size = this.f1178b.size();
        for (int i = 0; i < size; i++) {
            b bVar = this.f1178b.get(i);
            int i2 = bVar.f1181a;
            if (i2 == 1) {
                b(bVar);
            } else if (i2 == 2) {
                d(bVar);
            } else if (i2 == 4) {
                e(bVar);
            } else if (i2 == 8) {
                c(bVar);
            }
            Runnable runnable = this.e;
            if (runnable != null) {
                runnable.run();
            }
        }
        this.f1178b.clear();
    }

    /* access modifiers changed from: package-private */
    public void f() {
        a((List<b>) this.f1178b);
        a((List<b>) this.f1179c);
        this.h = 0;
    }
}
