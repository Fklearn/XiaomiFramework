package androidx.lifecycle;

import androidx.annotation.Nullable;
import androidx.lifecycle.f;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class a {

    /* renamed from: a  reason: collision with root package name */
    static a f976a = new a();

    /* renamed from: b  reason: collision with root package name */
    private final Map<Class, C0017a> f977b = new HashMap();

    /* renamed from: c  reason: collision with root package name */
    private final Map<Class, Boolean> f978c = new HashMap();

    /* renamed from: androidx.lifecycle.a$a  reason: collision with other inner class name */
    static class C0017a {

        /* renamed from: a  reason: collision with root package name */
        final Map<f.a, List<b>> f979a = new HashMap();

        /* renamed from: b  reason: collision with root package name */
        final Map<b, f.a> f980b;

        C0017a(Map<b, f.a> map) {
            this.f980b = map;
            for (Map.Entry next : map.entrySet()) {
                f.a aVar = (f.a) next.getValue();
                List list = this.f979a.get(aVar);
                if (list == null) {
                    list = new ArrayList();
                    this.f979a.put(aVar, list);
                }
                list.add(next.getKey());
            }
        }

        private static void a(List<b> list, i iVar, f.a aVar, Object obj) {
            if (list != null) {
                for (int size = list.size() - 1; size >= 0; size--) {
                    list.get(size).a(iVar, aVar, obj);
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void a(i iVar, f.a aVar, Object obj) {
            a(this.f979a.get(aVar), iVar, aVar, obj);
            a(this.f979a.get(f.a.ON_ANY), iVar, aVar, obj);
        }
    }

    static class b {

        /* renamed from: a  reason: collision with root package name */
        final int f981a;

        /* renamed from: b  reason: collision with root package name */
        final Method f982b;

        b(int i, Method method) {
            this.f981a = i;
            this.f982b = method;
            this.f982b.setAccessible(true);
        }

        /* access modifiers changed from: package-private */
        public void a(i iVar, f.a aVar, Object obj) {
            try {
                int i = this.f981a;
                if (i == 0) {
                    this.f982b.invoke(obj, new Object[0]);
                } else if (i == 1) {
                    this.f982b.invoke(obj, new Object[]{iVar});
                } else if (i == 2) {
                    this.f982b.invoke(obj, new Object[]{iVar, aVar});
                }
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Failed to call observer method", e.getCause());
            } catch (IllegalAccessException e2) {
                throw new RuntimeException(e2);
            }
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || b.class != obj.getClass()) {
                return false;
            }
            b bVar = (b) obj;
            return this.f981a == bVar.f981a && this.f982b.getName().equals(bVar.f982b.getName());
        }

        public int hashCode() {
            return (this.f981a * 31) + this.f982b.getName().hashCode();
        }
    }

    a() {
    }

    private C0017a a(Class cls, @Nullable Method[] methodArr) {
        int i;
        C0017a a2;
        Class superclass = cls.getSuperclass();
        HashMap hashMap = new HashMap();
        if (!(superclass == null || (a2 = a(superclass)) == null)) {
            hashMap.putAll(a2.f980b);
        }
        for (Class a3 : cls.getInterfaces()) {
            for (Map.Entry next : a(a3).f980b.entrySet()) {
                a(hashMap, (b) next.getKey(), (f.a) next.getValue(), cls);
            }
        }
        if (methodArr == null) {
            methodArr = c(cls);
        }
        boolean z = false;
        for (Method method : methodArr) {
            OnLifecycleEvent onLifecycleEvent = (OnLifecycleEvent) method.getAnnotation(OnLifecycleEvent.class);
            if (onLifecycleEvent != null) {
                Class[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length <= 0) {
                    i = 0;
                } else if (parameterTypes[0].isAssignableFrom(i.class)) {
                    i = 1;
                } else {
                    throw new IllegalArgumentException("invalid parameter type. Must be one and instanceof LifecycleOwner");
                }
                f.a value = onLifecycleEvent.value();
                if (parameterTypes.length > 1) {
                    if (!parameterTypes[1].isAssignableFrom(f.a.class)) {
                        throw new IllegalArgumentException("invalid parameter type. second arg must be an event");
                    } else if (value == f.a.ON_ANY) {
                        i = 2;
                    } else {
                        throw new IllegalArgumentException("Second arg is supported only for ON_ANY value");
                    }
                }
                if (parameterTypes.length <= 2) {
                    a(hashMap, new b(i, method), value, cls);
                    z = true;
                } else {
                    throw new IllegalArgumentException("cannot have more than 2 params");
                }
            }
        }
        C0017a aVar = new C0017a(hashMap);
        this.f977b.put(cls, aVar);
        this.f978c.put(cls, Boolean.valueOf(z));
        return aVar;
    }

    private void a(Map<b, f.a> map, b bVar, f.a aVar, Class cls) {
        f.a aVar2 = map.get(bVar);
        if (aVar2 != null && aVar != aVar2) {
            Method method = bVar.f982b;
            throw new IllegalArgumentException("Method " + method.getName() + " in " + cls.getName() + " already declared with different @OnLifecycleEvent value: previous value " + aVar2 + ", new value " + aVar);
        } else if (aVar2 == null) {
            map.put(bVar, aVar);
        }
    }

    private Method[] c(Class cls) {
        try {
            return cls.getDeclaredMethods();
        } catch (NoClassDefFoundError e) {
            throw new IllegalArgumentException("The observer class has some methods that use newer APIs which are not available in the current OS version. Lifecycles cannot access even other methods so you should make sure that your observer classes only access framework classes that are available in your min API level OR use lifecycle:compiler annotation processor.", e);
        }
    }

    /* access modifiers changed from: package-private */
    public C0017a a(Class cls) {
        C0017a aVar = this.f977b.get(cls);
        return aVar != null ? aVar : a(cls, (Method[]) null);
    }

    /* access modifiers changed from: package-private */
    public boolean b(Class cls) {
        Boolean bool = this.f978c.get(cls);
        if (bool != null) {
            return bool.booleanValue();
        }
        Method[] c2 = c(cls);
        for (Method annotation : c2) {
            if (((OnLifecycleEvent) annotation.getAnnotation(OnLifecycleEvent.class)) != null) {
                a(cls, c2);
                return true;
            }
        }
        this.f978c.put(cls, false);
        return false;
    }
}
