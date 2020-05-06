package d.a.i;

import android.animation.ArgbEvaluator;
import android.view.View;
import android.view.ViewTreeObserver;
import d.a.d;
import d.a.i.b;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.Map;

public class a {

    /* renamed from: a  reason: collision with root package name */
    public static final b.a f8776a = b.a(-2, 0.85f, 0.3f);

    /* renamed from: b  reason: collision with root package name */
    public static final ArgbEvaluator f8777b = new ArgbEvaluator();

    /* renamed from: c  reason: collision with root package name */
    private static final Class<?>[] f8778c = {String.class, Integer.TYPE, Integer.class, Long.TYPE, Long.class, Short.TYPE, Short.class, Float.TYPE, Float.class, Double.TYPE, Double.class};

    /* renamed from: d.a.i.a$a  reason: collision with other inner class name */
    private static class C0074a implements ViewTreeObserver.OnPreDrawListener {

        /* renamed from: a  reason: collision with root package name */
        Runnable f8779a;

        /* renamed from: b  reason: collision with root package name */
        WeakReference<View> f8780b;

        C0074a(Runnable runnable) {
            this.f8779a = runnable;
        }

        public void a(View view) {
            ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
            this.f8780b = new WeakReference<>(view);
            viewTreeObserver.addOnPreDrawListener(this);
        }

        public boolean onPreDraw() {
            View view = (View) this.f8780b.get();
            if (view != null) {
                Runnable runnable = this.f8779a;
                if (runnable != null) {
                    runnable.run();
                }
                view.getViewTreeObserver().removeOnPreDrawListener(this);
            }
            this.f8779a = null;
            return true;
        }
    }

    public static float a(d dVar, int i) {
        if (i == 0) {
            i = 6;
        } else if (i == 1) {
            i = 5;
        } else if (!(i == 6 || i == 5)) {
            i = -1;
        }
        if (i == -1) {
            return 0.0f;
        }
        return dVar.getValue(i);
    }

    public static <K, V> StringBuilder a(Map<K, V> map, String str) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        if (map != null && map.size() > 0) {
            for (Map.Entry next : map.entrySet()) {
                sb.append(10);
                sb.append(str);
                sb.append(next.getKey());
                sb.append('=');
                sb.append(next.getValue());
            }
            sb.append(10);
        }
        sb.append('}');
        return sb;
    }

    public static void a(View view, Runnable runnable) {
        if (view != null) {
            new C0074a(runnable).a(view);
        }
    }

    public static boolean a(long j, long j2) {
        return (j & j2) != 0;
    }

    public static boolean a(Class<?> cls) {
        return a((T[]) f8778c, cls);
    }

    public static <T> boolean a(T[] tArr) {
        return tArr == null || tArr.length == 0;
    }

    public static <T> boolean a(T[] tArr, T t) {
        if (!(t == null || tArr == null || tArr.length <= 0)) {
            for (T equals : tArr) {
                if (equals.equals(t)) {
                    return true;
                }
            }
        }
        return false;
    }

    @SafeVarargs
    public static <T> T[] a(T[] tArr, T... tArr2) {
        if (tArr == null) {
            return tArr2;
        }
        if (tArr2 == null) {
            return tArr;
        }
        Object newInstance = Array.newInstance(tArr.getClass().getComponentType(), tArr.length + tArr2.length);
        System.arraycopy(tArr, 0, newInstance, 0, tArr.length);
        System.arraycopy(tArr2, 0, newInstance, tArr.length, tArr2.length);
        return (Object[]) newInstance;
    }
}
