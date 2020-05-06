package androidx.recyclerview.widget;

import android.annotation.SuppressLint;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

/* renamed from: androidx.recyclerview.widget.t  reason: case insensitive filesystem */
final class C0178t implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    static final ThreadLocal<C0178t> f1254a = new ThreadLocal<>();

    /* renamed from: b  reason: collision with root package name */
    static Comparator<b> f1255b = new C0177s();

    /* renamed from: c  reason: collision with root package name */
    ArrayList<RecyclerView> f1256c = new ArrayList<>();

    /* renamed from: d  reason: collision with root package name */
    long f1257d;
    long e;
    private ArrayList<b> f = new ArrayList<>();

    @SuppressLint({"VisibleForTests"})
    /* renamed from: androidx.recyclerview.widget.t$a */
    static class a implements RecyclerView.g.a {

        /* renamed from: a  reason: collision with root package name */
        int f1258a;

        /* renamed from: b  reason: collision with root package name */
        int f1259b;

        /* renamed from: c  reason: collision with root package name */
        int[] f1260c;

        /* renamed from: d  reason: collision with root package name */
        int f1261d;

        a() {
        }

        /* access modifiers changed from: package-private */
        public void a() {
            int[] iArr = this.f1260c;
            if (iArr != null) {
                Arrays.fill(iArr, -1);
            }
            this.f1261d = 0;
        }

        public void a(int i, int i2) {
            if (i < 0) {
                throw new IllegalArgumentException("Layout positions must be non-negative");
            } else if (i2 >= 0) {
                int i3 = this.f1261d * 2;
                int[] iArr = this.f1260c;
                if (iArr == null) {
                    this.f1260c = new int[4];
                    Arrays.fill(this.f1260c, -1);
                } else if (i3 >= iArr.length) {
                    this.f1260c = new int[(i3 * 2)];
                    System.arraycopy(iArr, 0, this.f1260c, 0, iArr.length);
                }
                int[] iArr2 = this.f1260c;
                iArr2[i3] = i;
                iArr2[i3 + 1] = i2;
                this.f1261d++;
            } else {
                throw new IllegalArgumentException("Pixel distance must be non-negative");
            }
        }

        /* access modifiers changed from: package-private */
        public void a(RecyclerView recyclerView, boolean z) {
            this.f1261d = 0;
            int[] iArr = this.f1260c;
            if (iArr != null) {
                Arrays.fill(iArr, -1);
            }
            RecyclerView.g gVar = recyclerView.v;
            if (recyclerView.u != null && gVar != null && gVar.w()) {
                if (z) {
                    if (!recyclerView.m.c()) {
                        gVar.a(recyclerView.u.getItemCount(), (RecyclerView.g.a) this);
                    }
                } else if (!recyclerView.j()) {
                    gVar.a(this.f1258a, this.f1259b, recyclerView.qa, (RecyclerView.g.a) this);
                }
                int i = this.f1261d;
                if (i > gVar.m) {
                    gVar.m = i;
                    gVar.n = z;
                    recyclerView.k.j();
                }
            }
        }

        /* access modifiers changed from: package-private */
        public boolean a(int i) {
            if (this.f1260c != null) {
                int i2 = this.f1261d * 2;
                for (int i3 = 0; i3 < i2; i3 += 2) {
                    if (this.f1260c[i3] == i) {
                        return true;
                    }
                }
            }
            return false;
        }

        /* access modifiers changed from: package-private */
        public void b(int i, int i2) {
            this.f1258a = i;
            this.f1259b = i2;
        }
    }

    /* renamed from: androidx.recyclerview.widget.t$b */
    static class b {

        /* renamed from: a  reason: collision with root package name */
        public boolean f1262a;

        /* renamed from: b  reason: collision with root package name */
        public int f1263b;

        /* renamed from: c  reason: collision with root package name */
        public int f1264c;

        /* renamed from: d  reason: collision with root package name */
        public RecyclerView f1265d;
        public int e;

        b() {
        }

        public void a() {
            this.f1262a = false;
            this.f1263b = 0;
            this.f1264c = 0;
            this.f1265d = null;
            this.e = 0;
        }
    }

    C0178t() {
    }

    private RecyclerView.u a(RecyclerView recyclerView, int i, long j) {
        if (a(recyclerView, i)) {
            return null;
        }
        RecyclerView.n nVar = recyclerView.k;
        try {
            recyclerView.r();
            RecyclerView.u a2 = nVar.a(i, false, j);
            if (a2 != null) {
                if (!a2.isBound() || a2.isInvalid()) {
                    nVar.a(a2, false);
                } else {
                    nVar.b(a2.itemView);
                }
            }
            return a2;
        } finally {
            recyclerView.a(false);
        }
    }

    private void a() {
        b bVar;
        int size = this.f1256c.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            RecyclerView recyclerView = this.f1256c.get(i2);
            if (recyclerView.getWindowVisibility() == 0) {
                recyclerView.pa.a(recyclerView, false);
                i += recyclerView.pa.f1261d;
            }
        }
        this.f.ensureCapacity(i);
        int i3 = 0;
        for (int i4 = 0; i4 < size; i4++) {
            RecyclerView recyclerView2 = this.f1256c.get(i4);
            if (recyclerView2.getWindowVisibility() == 0) {
                a aVar = recyclerView2.pa;
                int abs = Math.abs(aVar.f1258a) + Math.abs(aVar.f1259b);
                int i5 = i3;
                for (int i6 = 0; i6 < aVar.f1261d * 2; i6 += 2) {
                    if (i5 >= this.f.size()) {
                        bVar = new b();
                        this.f.add(bVar);
                    } else {
                        bVar = this.f.get(i5);
                    }
                    int i7 = aVar.f1260c[i6 + 1];
                    bVar.f1262a = i7 <= abs;
                    bVar.f1263b = abs;
                    bVar.f1264c = i7;
                    bVar.f1265d = recyclerView2;
                    bVar.e = aVar.f1260c[i6];
                    i5++;
                }
                i3 = i5;
            }
        }
        Collections.sort(this.f, f1255b);
    }

    private void a(@Nullable RecyclerView recyclerView, long j) {
        if (recyclerView != null) {
            if (recyclerView.M && recyclerView.n.b() != 0) {
                recyclerView.u();
            }
            a aVar = recyclerView.pa;
            aVar.a(recyclerView, true);
            if (aVar.f1261d != 0) {
                try {
                    a.d.c.a.a("RV Nested Prefetch");
                    recyclerView.qa.a(recyclerView.u);
                    for (int i = 0; i < aVar.f1261d * 2; i += 2) {
                        a(recyclerView, aVar.f1260c[i], j);
                    }
                } finally {
                    a.d.c.a.a();
                }
            }
        }
    }

    private void a(b bVar, long j) {
        RecyclerView.u a2 = a(bVar.f1265d, bVar.e, bVar.f1262a ? Long.MAX_VALUE : j);
        if (a2 != null && a2.mNestedRecyclerView != null && a2.isBound() && !a2.isInvalid()) {
            a((RecyclerView) a2.mNestedRecyclerView.get(), j);
        }
    }

    static boolean a(RecyclerView recyclerView, int i) {
        int b2 = recyclerView.n.b();
        for (int i2 = 0; i2 < b2; i2++) {
            RecyclerView.u h = RecyclerView.h(recyclerView.n.d(i2));
            if (h.mPosition == i && !h.isInvalid()) {
                return true;
            }
        }
        return false;
    }

    private void b(long j) {
        int i = 0;
        while (i < this.f.size()) {
            b bVar = this.f.get(i);
            if (bVar.f1265d != null) {
                a(bVar, j);
                bVar.a();
                i++;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void a(long j) {
        a();
        b(j);
    }

    public void a(RecyclerView recyclerView) {
        this.f1256c.add(recyclerView);
    }

    /* access modifiers changed from: package-private */
    public void a(RecyclerView recyclerView, int i, int i2) {
        if (recyclerView.isAttachedToWindow() && this.f1257d == 0) {
            this.f1257d = recyclerView.getNanoTime();
            recyclerView.post(this);
        }
        recyclerView.pa.b(i, i2);
    }

    public void b(RecyclerView recyclerView) {
        this.f1256c.remove(recyclerView);
    }

    public void run() {
        try {
            a.d.c.a.a("RV Prefetch");
            if (!this.f1256c.isEmpty()) {
                int size = this.f1256c.size();
                long j = 0;
                for (int i = 0; i < size; i++) {
                    RecyclerView recyclerView = this.f1256c.get(i);
                    if (recyclerView.getWindowVisibility() == 0) {
                        j = Math.max(recyclerView.getDrawingTime(), j);
                    }
                }
                if (j != 0) {
                    a(TimeUnit.MILLISECONDS.toNanos(j) + this.e);
                    this.f1257d = 0;
                    a.d.c.a.a();
                }
            }
        } finally {
            this.f1257d = 0;
            a.d.c.a.a();
        }
    }
}
