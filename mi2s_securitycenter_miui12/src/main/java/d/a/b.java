package d.a;

import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import d.a.b.c;
import d.a.b.f;
import d.a.b.j;
import d.a.b.k;
import d.a.b.m;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private static final ConcurrentHashMap<d, a> f8632a = new ConcurrentHashMap<>();

    private static class a implements f {

        /* renamed from: a  reason: collision with root package name */
        private h f8637a;

        /* renamed from: b  reason: collision with root package name */
        private j f8638b;

        /* renamed from: c  reason: collision with root package name */
        private l f8639c;

        /* renamed from: d  reason: collision with root package name */
        private d[] f8640d;

        private a(d... dVarArr) {
            this.f8640d = dVarArr;
        }

        /* access modifiers changed from: package-private */
        public void a() {
            j jVar = this.f8638b;
            if (jVar != null) {
                jVar.clean();
            }
            l lVar = this.f8639c;
            if (lVar != null) {
                lVar.clean();
            }
            h hVar = this.f8637a;
            if (hVar != null) {
                hVar.clean();
            }
        }

        /* access modifiers changed from: package-private */
        public void b() {
            j jVar = this.f8638b;
            if (jVar != null) {
                jVar.a(new Object[0]);
            }
            l lVar = this.f8639c;
            if (lVar != null) {
                lVar.a(new Object[0]);
            }
            h hVar = this.f8637a;
            if (hVar != null) {
                hVar.a(new Object[0]);
            }
        }

        public h state() {
            if (this.f8637a == null) {
                this.f8637a = f.a(this.f8640d);
            }
            return this.f8637a;
        }

        public j touch() {
            if (this.f8638b == null) {
                j jVar = new j(this.f8640d);
                jVar.a(new c());
                this.f8638b = jVar;
            }
            return this.f8638b;
        }

        public l visible() {
            if (this.f8639c == null) {
                this.f8639c = new k(this.f8640d);
            }
            return this.f8639c;
        }
    }

    private static a a(View[] viewArr, d[] dVarArr) {
        a aVar = null;
        boolean z = false;
        for (int i = 0; i < viewArr.length; i++) {
            dVarArr[i] = a(viewArr[i], r.f8807b);
            a aVar2 = f8632a.get(dVarArr[i]);
            if (aVar == null) {
                aVar = aVar2;
            } else if (aVar != aVar2) {
                z = true;
            }
        }
        if (z) {
            return null;
        }
        return aVar;
    }

    public static <T> d a(T t, i<T> iVar) {
        if (t instanceof d) {
            return (d) t;
        }
        HashSet hashSet = new HashSet();
        a((Collection<d>) hashSet);
        Iterator it = hashSet.iterator();
        while (it.hasNext()) {
            d dVar = (d) it.next();
            Object targetObject = dVar.getTargetObject();
            if (targetObject != null && targetObject.equals(t)) {
                return dVar;
            }
        }
        if (iVar != null) {
            return iVar.createTarget(t);
        }
        return null;
    }

    public static f a(d dVar) {
        a aVar = f8632a.get(dVar);
        if (aVar != null) {
            return aVar;
        }
        a aVar2 = new a(new d[]{dVar});
        f8632a.put(dVar, aVar2);
        return aVar2;
    }

    public static f a(View... viewArr) {
        if (viewArr.length != 0) {
            if (viewArr.length == 1) {
                return a(a(viewArr[0], r.f8807b));
            }
            d[] dVarArr = new d[viewArr.length];
            a a2 = a(viewArr, dVarArr);
            if (a2 == null) {
                a2 = new a(dVarArr);
                for (d put : dVarArr) {
                    f8632a.put(put, a2);
                }
            }
            return a2;
        }
        throw new IllegalArgumentException("useAt can not be applied to empty views array");
    }

    public static <T> o a(T t) {
        return (o) a(t, o.f8801a);
    }

    public static void a(AbsListView absListView, MotionEvent motionEvent) {
        m a2 = j.a(absListView);
        if (a2 != null) {
            a2.onTouch(absListView, motionEvent);
        }
    }

    public static <T> void a(T t, Runnable runnable) {
        d a2 = a(t, (i) null);
        if (a2 != null) {
            a2.post(runnable);
        }
    }

    public static void a(Collection<d> collection) {
        for (d next : f8632a.keySet()) {
            if (next.isValid()) {
                collection.add(next);
            } else {
                f8632a.remove(next);
            }
        }
    }

    @SafeVarargs
    public static <T> void a(T... tArr) {
        for (T b2 : tArr) {
            b(b2);
        }
    }

    public static boolean a(View view) {
        return view.getTag(d.d.b.miuix_animation_tag_is_dragging) != null;
    }

    private static <T> void b(T t) {
        a remove;
        d a2 = a(t, (i) null);
        if (a2 != null && (remove = f8632a.remove(a2)) != null) {
            remove.a();
        }
    }

    public static <T> void b(T... tArr) {
        a aVar;
        for (T a2 : tArr) {
            d a3 = a(a2, (i) null);
            if (!(a3 == null || (aVar = f8632a.get(a3)) == null)) {
                aVar.b();
            }
        }
    }

    public static h c(Object... objArr) {
        d dVar;
        if (objArr.length > 0) {
            dVar = a(objArr[0], o.f8801a);
        } else {
            dVar = new o();
            dVar.setFlags(1);
        }
        return a(dVar).state();
    }
}
