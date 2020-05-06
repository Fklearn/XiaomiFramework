package b.d.e.c;

import android.util.Log;
import b.d.e.b;
import b.d.e.g;
import b.d.e.i;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class e {

    /* renamed from: a  reason: collision with root package name */
    private static f[] f2196a;

    /* renamed from: b  reason: collision with root package name */
    private g f2197b = null;

    /* renamed from: c  reason: collision with root package name */
    public g[] f2198c;

    /* renamed from: d  reason: collision with root package name */
    private int f2199d;
    private int[] e;
    private int f;
    private int[] g;
    private boolean h;

    public e() {
        int i = 0;
        this.h = false;
        if (f2196a == null) {
            f2196a = new f[]{new j(), new b(), new c()};
        }
        this.f2198c = new g[]{new i(), new h()};
        this.f2199d = 0;
        this.e = new int[f2196a.length];
        while (true) {
            f[] fVarArr = f2196a;
            if (i < fVarArr.length) {
                int a2 = fVarArr[i].a();
                int[] iArr = this.e;
                int i2 = this.f2199d;
                iArr[i] = i2;
                this.f2199d = i2 + a2;
                i++;
            } else {
                return;
            }
        }
    }

    private void c(b bVar) {
        List<String> d2 = bVar.d();
        TreeSet treeSet = new TreeSet();
        for (String a2 : d2) {
            for (String str : this.f2197b.a(a2)) {
                if (i.a(str) || this.f2197b.b(str)) {
                    treeSet.add(str);
                }
            }
        }
        bVar.a((Set<String>) treeSet);
    }

    public int a() {
        if (this.h) {
            return this.f + this.f2199d;
        }
        Log.e("RuleManager", "getRuleCount failed for having not be initialized.");
        return 0;
    }

    public void a(b bVar) {
        List arrayList = new ArrayList();
        arrayList.add(bVar.c());
        int[] iArr = new int[this.f2199d];
        int i = 0;
        while (true) {
            f[] fVarArr = f2196a;
            if (i >= fVarArr.length) {
                bVar.a((List<String>) arrayList);
                bVar.a(iArr);
                return;
            }
            fVarArr[i].b();
            arrayList = f2196a[i].a((List<String>) arrayList);
            f2196a[i].b(bVar, iArr, this.e[i]);
            i++;
        }
    }

    public void a(g gVar) {
        this.f2197b = gVar;
    }

    public void b(b bVar) {
        if (bVar.g() == null) {
            c(bVar);
            ArrayList arrayList = new ArrayList();
            arrayList.addAll(bVar.g());
            Collections.sort(arrayList);
            bVar.b((List<String>) arrayList);
        }
        int[] iArr = new int[a()];
        int[] e2 = bVar.e();
        int[] iArr2 = new int[a()];
        int i = 0;
        for (int i2 = 0; i2 < this.f2199d; i2++) {
            iArr2[i2] = e2[i2];
        }
        while (true) {
            g[] gVarArr = this.f2198c;
            if (i >= gVarArr.length) {
                bVar.a(iArr2);
                return;
            } else {
                gVarArr[i].b(bVar, iArr2, this.f2199d + this.g[i]);
                i++;
            }
        }
    }

    public g[] b() {
        return this.f2198c;
    }

    public void c() {
        int i = 0;
        this.f = 0;
        this.g = new int[this.f2198c.length];
        while (true) {
            g[] gVarArr = this.f2198c;
            if (i >= gVarArr.length) {
                this.h = true;
                return;
            }
            int a2 = gVarArr[i].a();
            int[] iArr = this.g;
            int i2 = this.f;
            iArr[i] = i2;
            this.f = i2 + a2;
            i++;
        }
    }
}
