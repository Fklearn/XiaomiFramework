package androidx.fragment.app;

import a.c.b;
import android.graphics.Rect;
import android.os.Build;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.app.i;
import androidx.core.view.ViewCompat;
import androidx.core.view.r;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

class E {

    /* renamed from: a  reason: collision with root package name */
    private static final int[] f873a = {0, 3, 0, 1, 5, 4, 7, 6, 9, 8, 10};

    /* renamed from: b  reason: collision with root package name */
    private static final N f874b = (Build.VERSION.SDK_INT >= 21 ? new J() : null);

    /* renamed from: c  reason: collision with root package name */
    private static final N f875c = a();

    static class a {

        /* renamed from: a  reason: collision with root package name */
        public Fragment f876a;

        /* renamed from: b  reason: collision with root package name */
        public boolean f877b;

        /* renamed from: c  reason: collision with root package name */
        public C0131a f878c;

        /* renamed from: d  reason: collision with root package name */
        public Fragment f879d;
        public boolean e;
        public C0131a f;

        a() {
        }
    }

    private static b<String, String> a(int i, ArrayList<C0131a> arrayList, ArrayList<Boolean> arrayList2, int i2, int i3) {
        ArrayList<String> arrayList3;
        ArrayList<String> arrayList4;
        b<String, String> bVar = new b<>();
        for (int i4 = i3 - 1; i4 >= i2; i4--) {
            C0131a aVar = arrayList.get(i4);
            if (aVar.b(i)) {
                boolean booleanValue = arrayList2.get(i4).booleanValue();
                ArrayList<String> arrayList5 = aVar.o;
                if (arrayList5 != null) {
                    int size = arrayList5.size();
                    if (booleanValue) {
                        arrayList3 = aVar.o;
                        arrayList4 = aVar.p;
                    } else {
                        ArrayList<String> arrayList6 = aVar.o;
                        arrayList3 = aVar.p;
                        arrayList4 = arrayList6;
                    }
                    for (int i5 = 0; i5 < size; i5++) {
                        String str = arrayList4.get(i5);
                        String str2 = arrayList3.get(i5);
                        String remove = bVar.remove(str2);
                        if (remove != null) {
                            bVar.put(str, remove);
                        } else {
                            bVar.put(str, str2);
                        }
                    }
                }
            }
        }
        return bVar;
    }

    static b<String, View> a(N n, b<String, String> bVar, Object obj, a aVar) {
        i iVar;
        ArrayList<String> arrayList;
        String a2;
        Fragment fragment = aVar.f876a;
        View E = fragment.E();
        if (bVar.isEmpty() || obj == null || E == null) {
            bVar.clear();
            return null;
        }
        b<String, View> bVar2 = new b<>();
        n.a((Map<String, View>) bVar2, E);
        C0131a aVar2 = aVar.f878c;
        if (aVar.f877b) {
            iVar = fragment.p();
            arrayList = aVar2.o;
        } else {
            iVar = fragment.n();
            arrayList = aVar2.p;
        }
        if (arrayList != null) {
            bVar2.a(arrayList);
            bVar2.a(bVar.values());
        }
        if (iVar != null) {
            iVar.a(arrayList, bVar2);
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                String str = arrayList.get(size);
                View view = bVar2.get(str);
                if (view == null) {
                    String a3 = a(bVar, str);
                    if (a3 != null) {
                        bVar.remove(a3);
                    }
                } else if (!str.equals(ViewCompat.m(view)) && (a2 = a(bVar, str)) != null) {
                    bVar.put(a2, ViewCompat.m(view));
                }
            }
        } else {
            a(bVar, bVar2);
        }
        return bVar2;
    }

    static View a(b<String, View> bVar, a aVar, Object obj, boolean z) {
        ArrayList<String> arrayList;
        C0131a aVar2 = aVar.f878c;
        if (obj == null || bVar == null || (arrayList = aVar2.o) == null || arrayList.isEmpty()) {
            return null;
        }
        return bVar.get((z ? aVar2.o : aVar2.p).get(0));
    }

    private static a a(a aVar, SparseArray<a> sparseArray, int i) {
        if (aVar != null) {
            return aVar;
        }
        a aVar2 = new a();
        sparseArray.put(i, aVar2);
        return aVar2;
    }

    private static N a() {
        try {
            return (N) Class.forName("androidx.transition.FragmentTransitionSupport").getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (Exception unused) {
            return null;
        }
    }

    private static N a(Fragment fragment, Fragment fragment2) {
        ArrayList arrayList = new ArrayList();
        if (fragment != null) {
            Object o = fragment.o();
            if (o != null) {
                arrayList.add(o);
            }
            Object z = fragment.z();
            if (z != null) {
                arrayList.add(z);
            }
            Object B = fragment.B();
            if (B != null) {
                arrayList.add(B);
            }
        }
        if (fragment2 != null) {
            Object m = fragment2.m();
            if (m != null) {
                arrayList.add(m);
            }
            Object w = fragment2.w();
            if (w != null) {
                arrayList.add(w);
            }
            Object A = fragment2.A();
            if (A != null) {
                arrayList.add(A);
            }
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        N n = f874b;
        if (n != null && a(n, (List<Object>) arrayList)) {
            return f874b;
        }
        N n2 = f875c;
        if (n2 != null && a(n2, (List<Object>) arrayList)) {
            return f875c;
        }
        if (f874b == null && f875c == null) {
            return null;
        }
        throw new IllegalArgumentException("Invalid Transition types");
    }

    private static Object a(N n, ViewGroup viewGroup, View view, b<String, String> bVar, a aVar, ArrayList<View> arrayList, ArrayList<View> arrayList2, Object obj, Object obj2) {
        b<String, String> bVar2;
        Object obj3;
        Object obj4;
        Rect rect;
        N n2 = n;
        a aVar2 = aVar;
        ArrayList<View> arrayList3 = arrayList;
        Object obj5 = obj;
        Fragment fragment = aVar2.f876a;
        Fragment fragment2 = aVar2.f879d;
        if (fragment == null || fragment2 == null) {
            return null;
        }
        boolean z = aVar2.f877b;
        if (bVar.isEmpty()) {
            bVar2 = bVar;
            obj3 = null;
        } else {
            obj3 = a(n2, fragment, fragment2, z);
            bVar2 = bVar;
        }
        b<String, View> b2 = b(n2, bVar2, obj3, aVar2);
        if (bVar.isEmpty()) {
            obj4 = null;
        } else {
            arrayList3.addAll(b2.values());
            obj4 = obj3;
        }
        if (obj5 == null && obj2 == null && obj4 == null) {
            return null;
        }
        a(fragment, fragment2, z, b2, true);
        if (obj4 != null) {
            rect = new Rect();
            n2.b(obj4, view, arrayList3);
            a(n, obj4, obj2, b2, aVar2.e, aVar2.f);
            if (obj5 != null) {
                n2.a(obj5, rect);
            }
        } else {
            rect = null;
        }
        D d2 = r0;
        D d3 = new D(n, bVar, obj4, aVar, arrayList2, view, fragment, fragment2, z, arrayList, obj, rect);
        r.a(viewGroup, d2);
        return obj4;
    }

    private static Object a(N n, Fragment fragment, Fragment fragment2, boolean z) {
        if (fragment == null || fragment2 == null) {
            return null;
        }
        return n.c(n.b(z ? fragment2.B() : fragment.A()));
    }

    private static Object a(N n, Fragment fragment, boolean z) {
        if (fragment == null) {
            return null;
        }
        return n.b(z ? fragment.w() : fragment.m());
    }

    private static Object a(N n, Object obj, Object obj2, Object obj3, Fragment fragment, boolean z) {
        return (obj == null || obj2 == null || fragment == null) ? true : z ? fragment.h() : fragment.g() ? n.b(obj2, obj, obj3) : n.a(obj2, obj, obj3);
    }

    private static String a(b<String, String> bVar, String str) {
        int size = bVar.size();
        for (int i = 0; i < size; i++) {
            if (str.equals(bVar.d(i))) {
                return bVar.b(i);
            }
        }
        return null;
    }

    static ArrayList<View> a(N n, Object obj, Fragment fragment, ArrayList<View> arrayList, View view) {
        if (obj == null) {
            return null;
        }
        ArrayList<View> arrayList2 = new ArrayList<>();
        View E = fragment.E();
        if (E != null) {
            n.a(arrayList2, E);
        }
        if (arrayList != null) {
            arrayList2.removeAll(arrayList);
        }
        if (arrayList2.isEmpty()) {
            return arrayList2;
        }
        arrayList2.add(view);
        n.a(obj, arrayList2);
        return arrayList2;
    }

    private static void a(b<String, String> bVar, b<String, View> bVar2) {
        for (int size = bVar.size() - 1; size >= 0; size--) {
            if (!bVar2.containsKey(bVar.d(size))) {
                bVar.c(size);
            }
        }
    }

    static void a(Fragment fragment, Fragment fragment2, boolean z, b<String, View> bVar, boolean z2) {
        i n = z ? fragment2.n() : fragment.n();
        if (n != null) {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            int size = bVar == null ? 0 : bVar.size();
            for (int i = 0; i < size; i++) {
                arrayList2.add(bVar.b(i));
                arrayList.add(bVar.d(i));
            }
            if (z2) {
                n.b(arrayList2, arrayList, (List<View>) null);
            } else {
                n.a(arrayList2, arrayList, (List<View>) null);
            }
        }
    }

    private static void a(N n, ViewGroup viewGroup, Fragment fragment, View view, ArrayList<View> arrayList, Object obj, ArrayList<View> arrayList2, Object obj2, ArrayList<View> arrayList3) {
        ViewGroup viewGroup2 = viewGroup;
        r.a(viewGroup, new B(obj, n, view, fragment, arrayList, arrayList2, arrayList3, obj2));
    }

    private static void a(N n, Object obj, Fragment fragment, ArrayList<View> arrayList) {
        if (fragment != null && obj != null && fragment.l && fragment.z && fragment.O) {
            fragment.g(true);
            n.a(obj, fragment.E(), arrayList);
            r.a(fragment.G, new A(arrayList));
        }
    }

    private static void a(N n, Object obj, Object obj2, b<String, View> bVar, boolean z, C0131a aVar) {
        ArrayList<String> arrayList = aVar.o;
        if (arrayList != null && !arrayList.isEmpty()) {
            View view = bVar.get((z ? aVar.p : aVar.o).get(0));
            n.c(obj, view);
            if (obj2 != null) {
                n.c(obj2, view);
            }
        }
    }

    public static void a(C0131a aVar, SparseArray<a> sparseArray, boolean z) {
        int size = aVar.f953a.size();
        for (int i = 0; i < size; i++) {
            a(aVar, aVar.f953a.get(i), sparseArray, false, z);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0041, code lost:
        if (r10.l != false) goto L_0x0095;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x0077, code lost:
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x0093, code lost:
        if (r10.z == false) goto L_0x0095;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x0095, code lost:
        r1 = true;
     */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x00a4  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x00e9 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:95:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void a(androidx.fragment.app.C0131a r16, androidx.fragment.app.z.a r17, android.util.SparseArray<androidx.fragment.app.E.a> r18, boolean r19, boolean r20) {
        /*
            r0 = r16
            r1 = r17
            r2 = r18
            r3 = r19
            androidx.fragment.app.Fragment r10 = r1.f958b
            if (r10 != 0) goto L_0x000d
            return
        L_0x000d:
            int r11 = r10.x
            if (r11 != 0) goto L_0x0012
            return
        L_0x0012:
            if (r3 == 0) goto L_0x001b
            int[] r4 = f873a
            int r1 = r1.f957a
            r1 = r4[r1]
            goto L_0x001d
        L_0x001b:
            int r1 = r1.f957a
        L_0x001d:
            r4 = 0
            r5 = 1
            if (r1 == r5) goto L_0x0088
            r6 = 3
            if (r1 == r6) goto L_0x0060
            r6 = 4
            if (r1 == r6) goto L_0x0048
            r6 = 5
            if (r1 == r6) goto L_0x0035
            r6 = 6
            if (r1 == r6) goto L_0x0060
            r6 = 7
            if (r1 == r6) goto L_0x0088
            r1 = r4
            r12 = r1
            r13 = r12
            goto L_0x009c
        L_0x0035:
            if (r20 == 0) goto L_0x0044
            boolean r1 = r10.O
            if (r1 == 0) goto L_0x0097
            boolean r1 = r10.z
            if (r1 != 0) goto L_0x0097
            boolean r1 = r10.l
            if (r1 == 0) goto L_0x0097
            goto L_0x0095
        L_0x0044:
            boolean r1 = r10.z
            goto L_0x0098
        L_0x0048:
            if (r20 == 0) goto L_0x0057
            boolean r1 = r10.O
            if (r1 == 0) goto L_0x0079
            boolean r1 = r10.l
            if (r1 == 0) goto L_0x0079
            boolean r1 = r10.z
            if (r1 == 0) goto L_0x0079
        L_0x0056:
            goto L_0x0077
        L_0x0057:
            boolean r1 = r10.l
            if (r1 == 0) goto L_0x0079
            boolean r1 = r10.z
            if (r1 != 0) goto L_0x0079
            goto L_0x0056
        L_0x0060:
            if (r20 == 0) goto L_0x007b
            boolean r1 = r10.l
            if (r1 != 0) goto L_0x0079
            android.view.View r1 = r10.H
            if (r1 == 0) goto L_0x0079
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x0079
            float r1 = r10.P
            r6 = 0
            int r1 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
            if (r1 < 0) goto L_0x0079
        L_0x0077:
            r1 = r5
            goto L_0x0084
        L_0x0079:
            r1 = r4
            goto L_0x0084
        L_0x007b:
            boolean r1 = r10.l
            if (r1 == 0) goto L_0x0079
            boolean r1 = r10.z
            if (r1 != 0) goto L_0x0079
            goto L_0x0077
        L_0x0084:
            r13 = r1
            r1 = r4
            r12 = r5
            goto L_0x009c
        L_0x0088:
            if (r20 == 0) goto L_0x008d
            boolean r1 = r10.N
            goto L_0x0098
        L_0x008d:
            boolean r1 = r10.l
            if (r1 != 0) goto L_0x0097
            boolean r1 = r10.z
            if (r1 != 0) goto L_0x0097
        L_0x0095:
            r1 = r5
            goto L_0x0098
        L_0x0097:
            r1 = r4
        L_0x0098:
            r12 = r4
            r13 = r12
            r4 = r1
            r1 = r5
        L_0x009c:
            java.lang.Object r6 = r2.get(r11)
            androidx.fragment.app.E$a r6 = (androidx.fragment.app.E.a) r6
            if (r4 == 0) goto L_0x00ae
            androidx.fragment.app.E$a r6 = a((androidx.fragment.app.E.a) r6, (android.util.SparseArray<androidx.fragment.app.E.a>) r2, (int) r11)
            r6.f876a = r10
            r6.f877b = r3
            r6.f878c = r0
        L_0x00ae:
            r14 = r6
            r15 = 0
            if (r20 != 0) goto L_0x00d5
            if (r1 == 0) goto L_0x00d5
            if (r14 == 0) goto L_0x00bc
            androidx.fragment.app.Fragment r1 = r14.f879d
            if (r1 != r10) goto L_0x00bc
            r14.f879d = r15
        L_0x00bc:
            androidx.fragment.app.t r4 = r0.s
            int r1 = r10.f883b
            if (r1 >= r5) goto L_0x00d5
            int r1 = r4.s
            if (r1 < r5) goto L_0x00d5
            boolean r1 = r0.q
            if (r1 != 0) goto L_0x00d5
            r4.j(r10)
            r6 = 1
            r7 = 0
            r8 = 0
            r9 = 0
            r5 = r10
            r4.a((androidx.fragment.app.Fragment) r5, (int) r6, (int) r7, (int) r8, (boolean) r9)
        L_0x00d5:
            if (r13 == 0) goto L_0x00e7
            if (r14 == 0) goto L_0x00dd
            androidx.fragment.app.Fragment r1 = r14.f879d
            if (r1 != 0) goto L_0x00e7
        L_0x00dd:
            androidx.fragment.app.E$a r14 = a((androidx.fragment.app.E.a) r14, (android.util.SparseArray<androidx.fragment.app.E.a>) r2, (int) r11)
            r14.f879d = r10
            r14.e = r3
            r14.f = r0
        L_0x00e7:
            if (r20 != 0) goto L_0x00f3
            if (r12 == 0) goto L_0x00f3
            if (r14 == 0) goto L_0x00f3
            androidx.fragment.app.Fragment r0 = r14.f876a
            if (r0 != r10) goto L_0x00f3
            r14.f876a = r15
        L_0x00f3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.fragment.app.E.a(androidx.fragment.app.a, androidx.fragment.app.z$a, android.util.SparseArray, boolean, boolean):void");
    }

    private static void a(t tVar, int i, a aVar, View view, b<String, String> bVar) {
        Fragment fragment;
        Fragment fragment2;
        N a2;
        Object obj;
        t tVar2 = tVar;
        a aVar2 = aVar;
        View view2 = view;
        b<String, String> bVar2 = bVar;
        ViewGroup viewGroup = tVar2.u.c() ? (ViewGroup) tVar2.u.a(i) : null;
        if (viewGroup != null && (a2 = a(fragment2, fragment)) != null) {
            boolean z = aVar2.f877b;
            boolean z2 = aVar2.e;
            Object a3 = a(a2, (fragment = aVar2.f876a), z);
            Object b2 = b(a2, (fragment2 = aVar2.f879d), z2);
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = arrayList;
            Object obj2 = b2;
            N n = a2;
            Object a4 = a(a2, viewGroup, view, bVar, aVar, (ArrayList<View>) arrayList, (ArrayList<View>) arrayList2, a3, obj2);
            Object obj3 = a3;
            if (obj3 == null && a4 == null) {
                obj = obj2;
                if (obj == null) {
                    return;
                }
            } else {
                obj = obj2;
            }
            ArrayList<View> a5 = a(n, obj, fragment2, (ArrayList<View>) arrayList3, view2);
            Object obj4 = (a5 == null || a5.isEmpty()) ? null : obj;
            n.a(obj3, view2);
            Object a6 = a(n, obj3, obj4, a4, fragment, aVar2.f877b);
            if (a6 != null) {
                ArrayList arrayList4 = new ArrayList();
                N n2 = n;
                n2.a(a6, obj3, arrayList4, obj4, a5, a4, arrayList2);
                a(n2, viewGroup, fragment, view, (ArrayList<View>) arrayList2, obj3, (ArrayList<View>) arrayList4, obj4, a5);
                ArrayList arrayList5 = arrayList2;
                n.a((View) viewGroup, (ArrayList<View>) arrayList5, (Map<String, String>) bVar2);
                n.a(viewGroup, a6);
                n.a(viewGroup, (ArrayList<View>) arrayList5, (Map<String, String>) bVar2);
            }
        }
    }

    static void a(t tVar, ArrayList<C0131a> arrayList, ArrayList<Boolean> arrayList2, int i, int i2, boolean z) {
        if (tVar.s >= 1) {
            SparseArray sparseArray = new SparseArray();
            for (int i3 = i; i3 < i2; i3++) {
                C0131a aVar = arrayList.get(i3);
                if (arrayList2.get(i3).booleanValue()) {
                    b(aVar, (SparseArray<a>) sparseArray, z);
                } else {
                    a(aVar, (SparseArray<a>) sparseArray, z);
                }
            }
            if (sparseArray.size() != 0) {
                View view = new View(tVar.t.f());
                int size = sparseArray.size();
                for (int i4 = 0; i4 < size; i4++) {
                    int keyAt = sparseArray.keyAt(i4);
                    b<String, String> a2 = a(keyAt, arrayList, arrayList2, i, i2);
                    a aVar2 = (a) sparseArray.valueAt(i4);
                    if (z) {
                        b(tVar, keyAt, aVar2, view, a2);
                    } else {
                        a(tVar, keyAt, aVar2, view, a2);
                    }
                }
            }
        }
    }

    static void a(ArrayList<View> arrayList, int i) {
        if (arrayList != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                arrayList.get(size).setVisibility(i);
            }
        }
    }

    private static void a(ArrayList<View> arrayList, b<String, View> bVar, Collection<String> collection) {
        for (int size = bVar.size() - 1; size >= 0; size--) {
            View d2 = bVar.d(size);
            if (collection.contains(ViewCompat.m(d2))) {
                arrayList.add(d2);
            }
        }
    }

    private static boolean a(N n, List<Object> list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (!n.a(list.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static b<String, View> b(N n, b<String, String> bVar, Object obj, a aVar) {
        i iVar;
        ArrayList<String> arrayList;
        if (bVar.isEmpty() || obj == null) {
            bVar.clear();
            return null;
        }
        Fragment fragment = aVar.f879d;
        b<String, View> bVar2 = new b<>();
        n.a((Map<String, View>) bVar2, fragment.fa());
        C0131a aVar2 = aVar.f;
        if (aVar.e) {
            iVar = fragment.n();
            arrayList = aVar2.p;
        } else {
            iVar = fragment.p();
            arrayList = aVar2.o;
        }
        bVar2.a(arrayList);
        if (iVar != null) {
            iVar.a(arrayList, bVar2);
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                String str = arrayList.get(size);
                View view = bVar2.get(str);
                if (view == null) {
                    bVar.remove(str);
                } else if (!str.equals(ViewCompat.m(view))) {
                    bVar.put(ViewCompat.m(view), bVar.remove(str));
                }
            }
        } else {
            bVar.a(bVar2.keySet());
        }
        return bVar2;
    }

    private static Object b(N n, ViewGroup viewGroup, View view, b<String, String> bVar, a aVar, ArrayList<View> arrayList, ArrayList<View> arrayList2, Object obj, Object obj2) {
        Object obj3;
        Rect rect;
        View view2;
        N n2 = n;
        View view3 = view;
        b<String, String> bVar2 = bVar;
        a aVar2 = aVar;
        ArrayList<View> arrayList3 = arrayList;
        ArrayList<View> arrayList4 = arrayList2;
        Object obj4 = obj;
        Fragment fragment = aVar2.f876a;
        Fragment fragment2 = aVar2.f879d;
        if (fragment != null) {
            fragment.fa().setVisibility(0);
        }
        if (fragment == null || fragment2 == null) {
            return null;
        }
        boolean z = aVar2.f877b;
        Object a2 = bVar.isEmpty() ? null : a(n, fragment, fragment2, z);
        b<String, View> b2 = b(n, bVar2, a2, aVar2);
        b<String, View> a3 = a(n, bVar2, a2, aVar2);
        if (bVar.isEmpty()) {
            if (b2 != null) {
                b2.clear();
            }
            if (a3 != null) {
                a3.clear();
            }
            obj3 = null;
        } else {
            a(arrayList3, b2, (Collection<String>) bVar.keySet());
            a(arrayList4, a3, bVar.values());
            obj3 = a2;
        }
        if (obj4 == null && obj2 == null && obj3 == null) {
            return null;
        }
        a(fragment, fragment2, z, b2, true);
        if (obj3 != null) {
            arrayList4.add(view3);
            n.b(obj3, view3, arrayList3);
            a(n, obj3, obj2, b2, aVar2.e, aVar2.f);
            Rect rect2 = new Rect();
            View a4 = a(a3, aVar2, obj4, z);
            if (a4 != null) {
                n.a(obj4, rect2);
            }
            rect = rect2;
            view2 = a4;
        } else {
            view2 = null;
            rect = null;
        }
        r.a(viewGroup, new C(fragment, fragment2, z, a3, view2, n, rect));
        return obj3;
    }

    private static Object b(N n, Fragment fragment, boolean z) {
        if (fragment == null) {
            return null;
        }
        return n.b(z ? fragment.z() : fragment.o());
    }

    public static void b(C0131a aVar, SparseArray<a> sparseArray, boolean z) {
        if (aVar.s.u.c()) {
            for (int size = aVar.f953a.size() - 1; size >= 0; size--) {
                a(aVar, aVar.f953a.get(size), sparseArray, true, z);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x001e, code lost:
        r11 = r4.f876a;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void b(androidx.fragment.app.t r17, int r18, androidx.fragment.app.E.a r19, android.view.View r20, a.c.b<java.lang.String, java.lang.String> r21) {
        /*
            r0 = r17
            r4 = r19
            r9 = r20
            androidx.fragment.app.h r1 = r0.u
            boolean r1 = r1.c()
            if (r1 == 0) goto L_0x0019
            androidx.fragment.app.h r0 = r0.u
            r1 = r18
            android.view.View r0 = r0.a(r1)
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            goto L_0x001a
        L_0x0019:
            r0 = 0
        L_0x001a:
            r10 = r0
            if (r10 != 0) goto L_0x001e
            return
        L_0x001e:
            androidx.fragment.app.Fragment r11 = r4.f876a
            androidx.fragment.app.Fragment r12 = r4.f879d
            androidx.fragment.app.N r13 = a((androidx.fragment.app.Fragment) r12, (androidx.fragment.app.Fragment) r11)
            if (r13 != 0) goto L_0x0029
            return
        L_0x0029:
            boolean r14 = r4.f877b
            boolean r0 = r4.e
            java.util.ArrayList r15 = new java.util.ArrayList
            r15.<init>()
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>()
            java.lang.Object r7 = a((androidx.fragment.app.N) r13, (androidx.fragment.app.Fragment) r11, (boolean) r14)
            java.lang.Object r6 = b((androidx.fragment.app.N) r13, (androidx.fragment.app.Fragment) r12, (boolean) r0)
            r0 = r13
            r1 = r10
            r2 = r20
            r3 = r21
            r4 = r19
            r5 = r8
            r17 = r6
            r6 = r15
            r18 = r7
            r16 = r10
            r10 = r8
            r8 = r17
            java.lang.Object r8 = b(r0, r1, r2, r3, r4, r5, r6, r7, r8)
            r6 = r18
            if (r6 != 0) goto L_0x0061
            if (r8 != 0) goto L_0x0061
            r7 = r17
            if (r7 != 0) goto L_0x0063
            return
        L_0x0061:
            r7 = r17
        L_0x0063:
            java.util.ArrayList r5 = a((androidx.fragment.app.N) r13, (java.lang.Object) r7, (androidx.fragment.app.Fragment) r12, (java.util.ArrayList<android.view.View>) r10, (android.view.View) r9)
            java.util.ArrayList r9 = a((androidx.fragment.app.N) r13, (java.lang.Object) r6, (androidx.fragment.app.Fragment) r11, (java.util.ArrayList<android.view.View>) r15, (android.view.View) r9)
            r0 = 4
            a((java.util.ArrayList<android.view.View>) r9, (int) r0)
            r0 = r13
            r1 = r6
            r2 = r7
            r3 = r8
            r4 = r11
            r11 = r5
            r5 = r14
            java.lang.Object r14 = a((androidx.fragment.app.N) r0, (java.lang.Object) r1, (java.lang.Object) r2, (java.lang.Object) r3, (androidx.fragment.app.Fragment) r4, (boolean) r5)
            if (r14 == 0) goto L_0x00a4
            a((androidx.fragment.app.N) r13, (java.lang.Object) r7, (androidx.fragment.app.Fragment) r12, (java.util.ArrayList<android.view.View>) r11)
            java.util.ArrayList r12 = r13.a((java.util.ArrayList<android.view.View>) r15)
            r0 = r13
            r1 = r14
            r2 = r6
            r3 = r9
            r4 = r7
            r5 = r11
            r6 = r8
            r7 = r15
            r0.a(r1, r2, r3, r4, r5, r6, r7)
            r0 = r16
            r13.a((android.view.ViewGroup) r0, (java.lang.Object) r14)
            r1 = r13
            r2 = r0
            r3 = r10
            r4 = r15
            r5 = r12
            r6 = r21
            r1.a(r2, r3, r4, r5, r6)
            r0 = 0
            a((java.util.ArrayList<android.view.View>) r9, (int) r0)
            r13.b((java.lang.Object) r8, (java.util.ArrayList<android.view.View>) r10, (java.util.ArrayList<android.view.View>) r15)
        L_0x00a4:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.fragment.app.E.b(androidx.fragment.app.t, int, androidx.fragment.app.E$a, android.view.View, a.c.b):void");
    }
}
