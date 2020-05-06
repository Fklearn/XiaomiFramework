package androidx.recyclerview.widget;

import a.c.f;
import a.c.i;
import a.d.e.d;
import a.d.e.e;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.RecyclerView;

class V {
    @VisibleForTesting

    /* renamed from: a  reason: collision with root package name */
    final i<RecyclerView.u, a> f1165a = new i<>();
    @VisibleForTesting

    /* renamed from: b  reason: collision with root package name */
    final f<RecyclerView.u> f1166b = new f<>();

    static class a {

        /* renamed from: a  reason: collision with root package name */
        static d<a> f1167a = new e(20);

        /* renamed from: b  reason: collision with root package name */
        int f1168b;
        @Nullable

        /* renamed from: c  reason: collision with root package name */
        RecyclerView.ItemAnimator.c f1169c;
        @Nullable

        /* renamed from: d  reason: collision with root package name */
        RecyclerView.ItemAnimator.c f1170d;

        private a() {
        }

        static void a() {
            do {
            } while (f1167a.acquire() != null);
        }

        static void a(a aVar) {
            aVar.f1168b = 0;
            aVar.f1169c = null;
            aVar.f1170d = null;
            f1167a.release(aVar);
        }

        static a b() {
            a acquire = f1167a.acquire();
            return acquire == null ? new a() : acquire;
        }
    }

    interface b {
        void a(RecyclerView.u uVar);

        void a(RecyclerView.u uVar, @Nullable RecyclerView.ItemAnimator.c cVar, RecyclerView.ItemAnimator.c cVar2);

        void b(RecyclerView.u uVar, @NonNull RecyclerView.ItemAnimator.c cVar, @Nullable RecyclerView.ItemAnimator.c cVar2);

        void c(RecyclerView.u uVar, @NonNull RecyclerView.ItemAnimator.c cVar, @NonNull RecyclerView.ItemAnimator.c cVar2);
    }

    V() {
    }

    private RecyclerView.ItemAnimator.c a(RecyclerView.u uVar, int i) {
        a d2;
        RecyclerView.ItemAnimator.c cVar;
        int a2 = this.f1165a.a((Object) uVar);
        if (a2 >= 0 && (d2 = this.f1165a.d(a2)) != null) {
            int i2 = d2.f1168b;
            if ((i2 & i) != 0) {
                d2.f1168b = (~i) & i2;
                if (i == 4) {
                    cVar = d2.f1169c;
                } else if (i == 8) {
                    cVar = d2.f1170d;
                } else {
                    throw new IllegalArgumentException("Must provide flag PRE or POST");
                }
                if ((d2.f1168b & 12) == 0) {
                    this.f1165a.c(a2);
                    a.a(d2);
                }
                return cVar;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public RecyclerView.u a(long j) {
        return this.f1166b.a(j);
    }

    /* access modifiers changed from: package-private */
    public void a() {
        this.f1165a.clear();
        this.f1166b.clear();
    }

    /* access modifiers changed from: package-private */
    public void a(long j, RecyclerView.u uVar) {
        this.f1166b.c(j, uVar);
    }

    /* access modifiers changed from: package-private */
    public void a(RecyclerView.u uVar) {
        a aVar = this.f1165a.get(uVar);
        if (aVar == null) {
            aVar = a.b();
            this.f1165a.put(uVar, aVar);
        }
        aVar.f1168b |= 1;
    }

    /* access modifiers changed from: package-private */
    public void a(RecyclerView.u uVar, RecyclerView.ItemAnimator.c cVar) {
        a aVar = this.f1165a.get(uVar);
        if (aVar == null) {
            aVar = a.b();
            this.f1165a.put(uVar, aVar);
        }
        aVar.f1168b |= 2;
        aVar.f1169c = cVar;
    }

    /* access modifiers changed from: package-private */
    public void a(b bVar) {
        RecyclerView.ItemAnimator.c cVar;
        RecyclerView.ItemAnimator.c cVar2;
        for (int size = this.f1165a.size() - 1; size >= 0; size--) {
            RecyclerView.u b2 = this.f1165a.b(size);
            a c2 = this.f1165a.c(size);
            int i = c2.f1168b;
            if ((i & 3) != 3) {
                if ((i & 1) != 0) {
                    cVar = c2.f1169c;
                    if (cVar != null) {
                        cVar2 = c2.f1170d;
                    }
                } else {
                    if ((i & 14) != 14) {
                        if ((i & 12) == 12) {
                            bVar.c(b2, c2.f1169c, c2.f1170d);
                        } else if ((i & 4) != 0) {
                            cVar = c2.f1169c;
                            cVar2 = null;
                        } else if ((i & 8) == 0) {
                        }
                        a.a(c2);
                    }
                    bVar.a(b2, c2.f1169c, c2.f1170d);
                    a.a(c2);
                }
                bVar.b(b2, cVar, cVar2);
                a.a(c2);
            }
            bVar.a(b2);
            a.a(c2);
        }
    }

    /* access modifiers changed from: package-private */
    public void b() {
        a.a();
    }

    /* access modifiers changed from: package-private */
    public void b(RecyclerView.u uVar, RecyclerView.ItemAnimator.c cVar) {
        a aVar = this.f1165a.get(uVar);
        if (aVar == null) {
            aVar = a.b();
            this.f1165a.put(uVar, aVar);
        }
        aVar.f1170d = cVar;
        aVar.f1168b |= 8;
    }

    /* access modifiers changed from: package-private */
    public boolean b(RecyclerView.u uVar) {
        a aVar = this.f1165a.get(uVar);
        return (aVar == null || (aVar.f1168b & 1) == 0) ? false : true;
    }

    /* access modifiers changed from: package-private */
    public void c(RecyclerView.u uVar, RecyclerView.ItemAnimator.c cVar) {
        a aVar = this.f1165a.get(uVar);
        if (aVar == null) {
            aVar = a.b();
            this.f1165a.put(uVar, aVar);
        }
        aVar.f1169c = cVar;
        aVar.f1168b |= 4;
    }

    /* access modifiers changed from: package-private */
    public boolean c(RecyclerView.u uVar) {
        a aVar = this.f1165a.get(uVar);
        return (aVar == null || (aVar.f1168b & 4) == 0) ? false : true;
    }

    public void d(RecyclerView.u uVar) {
        g(uVar);
    }

    /* access modifiers changed from: package-private */
    @Nullable
    public RecyclerView.ItemAnimator.c e(RecyclerView.u uVar) {
        return a(uVar, 8);
    }

    /* access modifiers changed from: package-private */
    @Nullable
    public RecyclerView.ItemAnimator.c f(RecyclerView.u uVar) {
        return a(uVar, 4);
    }

    /* access modifiers changed from: package-private */
    public void g(RecyclerView.u uVar) {
        a aVar = this.f1165a.get(uVar);
        if (aVar != null) {
            aVar.f1168b &= -2;
        }
    }

    /* access modifiers changed from: package-private */
    public void h(RecyclerView.u uVar) {
        int a2 = this.f1166b.a() - 1;
        while (true) {
            if (a2 < 0) {
                break;
            } else if (uVar == this.f1166b.c(a2)) {
                this.f1166b.b(a2);
                break;
            } else {
                a2--;
            }
        }
        a remove = this.f1165a.remove(uVar);
        if (remove != null) {
            a.a(remove);
        }
    }
}
