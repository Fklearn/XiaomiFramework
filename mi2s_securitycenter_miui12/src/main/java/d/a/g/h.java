package d.a.g;

import android.util.ArrayMap;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

public class h {

    /* renamed from: a  reason: collision with root package name */
    private Map<String, b> f8760a = new ArrayMap();

    /* renamed from: b  reason: collision with root package name */
    private Map<String, b> f8761b = new ArrayMap();

    /* renamed from: c  reason: collision with root package name */
    private Map<String, a> f8762c = new ArrayMap();

    /* renamed from: d  reason: collision with root package name */
    private Map<String, String> f8763d = new ArrayMap();
    private Map<String, String> e = new ArrayMap();
    private Object f;
    private WeakReference<Object> g;
    private Map<String, Object> h = new ArrayMap();

    private static class a {

        /* renamed from: a  reason: collision with root package name */
        Field f8764a;

        private a() {
        }
    }

    private static class b {

        /* renamed from: a  reason: collision with root package name */
        Method f8765a;

        private b() {
        }
    }

    public h(Object obj) {
        if (d.a.i.a.a(obj.getClass())) {
            this.f = obj;
        } else {
            this.g = new WeakReference<>(obj);
        }
    }

    private <T> T a(Object obj, Class<T> cls) {
        if (!(obj instanceof Number)) {
            return null;
        }
        Number number = (Number) obj;
        if (cls == Float.class || cls == Float.TYPE) {
            return Float.valueOf(number.floatValue());
        }
        if (cls == Integer.class || cls == Integer.TYPE) {
            return Integer.valueOf(number.intValue());
        }
        throw new IllegalArgumentException("getPropertyValue, clz must be float or int instead of " + cls);
    }

    private <T> T a(Object obj, Field field) {
        try {
            return field.get(obj);
        } catch (Exception e2) {
            Log.d("miuix_anim", "getValueByField failed", e2);
            return null;
        }
    }

    private <T> T a(Object obj, Method method, Object... objArr) {
        if (method == null) {
            return null;
        }
        try {
            return method.invoke(obj, objArr);
        } catch (Exception e2) {
            Log.d("miuix_anim", "ValueProperty.invokeMethod failed, " + method.getName(), e2);
            return null;
        }
    }

    private String a(String str, String str2, Map<String, String> map) {
        String str3 = map.get(str);
        if (str3 != null) {
            return str3;
        }
        String str4 = str2 + Character.toUpperCase(str.charAt(0)) + str.substring(1);
        map.put(str, str4);
        return str4;
    }

    private Field a(Object obj, String str, Class<?> cls) {
        a aVar = this.f8762c.get(str);
        if (aVar == null) {
            aVar = new a();
            aVar.f8764a = b(obj, str, cls);
            this.f8762c.put(str, aVar);
        }
        return aVar.f8764a;
    }

    private Method a(Object obj, String str, Map<String, b> map, Class<?>... clsArr) {
        b bVar = map.get(str);
        if (bVar == null) {
            bVar = new b();
            bVar.f8765a = a(obj, str, clsArr);
            map.put(str, bVar);
        }
        return bVar.f8765a;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:?, code lost:
        return r0;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x000e */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.reflect.Method a(java.lang.Object r3, java.lang.String r4, java.lang.Class<?>... r5) {
        /*
            r2 = this;
            r0 = 0
            java.lang.Class r1 = r3.getClass()     // Catch:{ NoSuchMethodException -> 0x000e }
            java.lang.reflect.Method r0 = r1.getDeclaredMethod(r4, r5)     // Catch:{ NoSuchMethodException -> 0x000e }
            r1 = 1
            r0.setAccessible(r1)     // Catch:{ NoSuchMethodException -> 0x000e }
            goto L_0x0016
        L_0x000e:
            java.lang.Class r3 = r3.getClass()     // Catch:{ NoSuchMethodException -> 0x0016 }
            java.lang.reflect.Method r0 = r3.getMethod(r4, r5)     // Catch:{ NoSuchMethodException -> 0x0016 }
        L_0x0016:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: d.a.g.h.a(java.lang.Object, java.lang.String, java.lang.Class[]):java.lang.reflect.Method");
    }

    private <T> void a(Object obj, Field field, T t) {
        try {
            field.set(obj, t);
        } catch (Exception e2) {
            Log.d("miuix_anim", "getValueByField failed", e2);
        }
    }

    private Object b() {
        WeakReference<Object> weakReference = this.g;
        if (weakReference != null) {
            return weakReference.get();
        }
        return null;
    }

    private <T> T b(String str, Class<T> cls, Object obj) {
        Method a2 = a(obj, a(str, "get", this.f8763d), this.f8760a, new Class[0]);
        if (a2 == null) {
            return null;
        }
        return a(a(obj, a2, new Object[0]), cls);
    }

    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:8:0x000f */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.reflect.Field b(java.lang.Object r4, java.lang.String r5, java.lang.Class<?> r6) {
        /*
            r3 = this;
            r0 = 0
            java.lang.Class r1 = r4.getClass()     // Catch:{ NoSuchFieldException -> 0x000e }
            java.lang.reflect.Field r1 = r1.getDeclaredField(r5)     // Catch:{ NoSuchFieldException -> 0x000e }
            r2 = 1
            r1.setAccessible(r2)     // Catch:{ NoSuchFieldException -> 0x000f }
            goto L_0x0018
        L_0x000e:
            r1 = r0
        L_0x000f:
            java.lang.Class r4 = r4.getClass()     // Catch:{ NoSuchFieldException -> 0x0018 }
            java.lang.reflect.Field r4 = r4.getField(r5)     // Catch:{ NoSuchFieldException -> 0x0018 }
            r1 = r4
        L_0x0018:
            if (r1 == 0) goto L_0x0021
            java.lang.Class r4 = r1.getType()
            if (r4 == r6) goto L_0x0021
            goto L_0x0022
        L_0x0021:
            r0 = r1
        L_0x0022:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: d.a.g.h.b(java.lang.Object, java.lang.String, java.lang.Class):java.lang.reflect.Field");
    }

    public <T> T a(String str, Class<T> cls) {
        Object b2 = b();
        if (this.f == null && b2 != null) {
            T b3 = b(str, cls, b2);
            if (b3 != null) {
                return b3;
            }
            Field a2 = a(b2, str, (Class<?>) cls);
            if (a2 != null) {
                return a(b2, a2);
            }
        }
        return this.h.get(str);
    }

    public <T> void a(String str, Class<T> cls, T t) {
        Object b2 = b();
        if (this.f == null && b2 != null) {
            Method a2 = a(b2, a(str, "set", this.e), this.f8761b, cls);
            if (a2 != null) {
                a(b2, a2, t);
                return;
            }
            Field a3 = a(b2, str, (Class<?>) cls);
            if (a3 != null) {
                a(b2, a3, t);
                return;
            }
        }
        this.h.put(str, t);
    }

    public boolean a() {
        return (this.f == null && b() == null) ? false : true;
    }

    public boolean equals(Object obj) {
        Object b2;
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != h.class) {
            Object obj2 = this.f;
            if (obj2 != null) {
                return Objects.equals(obj2, obj);
            }
            Object b3 = b();
            if (b3 != null) {
                return Objects.equals(b3, obj);
            }
            return false;
        }
        h hVar = (h) obj;
        Object obj3 = this.f;
        if (obj3 != null) {
            b2 = hVar.f;
        } else {
            obj3 = b();
            b2 = hVar.b();
        }
        return Objects.equals(obj3, b2);
    }

    public int hashCode() {
        Object obj = this.f;
        if (obj != null) {
            return obj.hashCode();
        }
        Object b2 = b();
        if (b2 != null) {
            return b2.hashCode();
        }
        return 0;
    }
}
