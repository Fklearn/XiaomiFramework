package b.b.o.g;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class c {

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        private Class f1889a;

        /* renamed from: b  reason: collision with root package name */
        private Object f1890b;

        /* renamed from: c  reason: collision with root package name */
        private Object f1891c;

        private a() {
        }

        public static a a(Object obj) {
            a aVar = new a();
            if (obj != null) {
                aVar.f1890b = obj;
                aVar.f1889a = obj.getClass();
            }
            return aVar;
        }

        public static a a(String str) {
            a aVar = new a();
            try {
                aVar.f1889a = Class.forName(str);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return aVar;
        }

        public a a(String str, Class<?>[] clsArr, Object... objArr) {
            Object obj = this.f1890b;
            if (obj != null) {
                try {
                    this.f1891c = c.a(obj, str, clsArr, objArr);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e2) {
                    e2.printStackTrace();
                } catch (InvocationTargetException e3) {
                    e3.printStackTrace();
                }
            }
            return this;
        }

        public a a(Class<?>[] clsArr, Object... objArr) {
            Class cls = this.f1889a;
            if (cls != null) {
                try {
                    this.f1890b = cls.getConstructor(clsArr).newInstance(objArr);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e2) {
                    e2.printStackTrace();
                } catch (InvocationTargetException e3) {
                    e3.printStackTrace();
                } catch (InstantiationException e4) {
                    e4.printStackTrace();
                }
            }
            return this;
        }

        public boolean a() {
            Object obj = this.f1891c;
            if (obj == null) {
                return false;
            }
            return ((Boolean) obj).booleanValue();
        }

        public a b(String str) {
            Object obj = this.f1890b;
            if (obj != null) {
                try {
                    this.f1891c = c.a(obj, str);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e2) {
                    e2.printStackTrace();
                }
            }
            return this;
        }

        public a b(String str, Class<?>[] clsArr, Object... objArr) {
            Class cls = this.f1889a;
            if (cls != null) {
                try {
                    this.f1891c = c.a((Class<?>) cls, str, clsArr, objArr);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e2) {
                    e2.printStackTrace();
                } catch (InvocationTargetException e3) {
                    e3.printStackTrace();
                }
            }
            return this;
        }

        public Object b() {
            return this.f1890b;
        }

        public int c() {
            Object obj = this.f1891c;
            if (obj == null) {
                return 0;
            }
            return ((Integer) obj).intValue();
        }

        public Object d() {
            Object obj = this.f1891c;
            if (obj == null) {
                return null;
            }
            return obj;
        }

        public a e() {
            this.f1890b = this.f1891c;
            this.f1891c = null;
            return this;
        }

        public String f() {
            Object obj = this.f1891c;
            if (obj == null) {
                return null;
            }
            return obj.toString();
        }
    }

    public static Object a(Class cls, Object obj, String str, Class<?>[] clsArr, Object... objArr) {
        Method declaredMethod = cls.getDeclaredMethod(str, clsArr);
        declaredMethod.setAccessible(true);
        return declaredMethod.invoke(obj, objArr);
    }

    public static Object a(Class<?> cls, String str) {
        Field declaredField = cls.getDeclaredField(str);
        declaredField.setAccessible(true);
        return declaredField.get((Object) null);
    }

    public static <T> T a(Class<?> cls, String str, Class<T> cls2) {
        Field declaredField = cls.getDeclaredField(str);
        declaredField.setAccessible(true);
        return declaredField.get((Object) null);
    }

    public static Object a(Class<?> cls, String str, Class<?>[] clsArr, Object... objArr) {
        Method declaredMethod = cls.getDeclaredMethod(str, clsArr);
        declaredMethod.setAccessible(true);
        return declaredMethod.invoke((Object) null, objArr);
    }

    public static Object a(Class<?> cls, Class<?>[] clsArr, Object... objArr) {
        if (cls != null) {
            return cls.getConstructor(clsArr).newInstance(objArr);
        }
        return null;
    }

    public static <T> T a(Object obj, Class<T> cls, String str, Class<?>[] clsArr, Object... objArr) {
        Method declaredMethod = obj.getClass().getDeclaredMethod(str, clsArr);
        declaredMethod.setAccessible(true);
        return declaredMethod.invoke(obj, objArr);
    }

    public static Object a(Object obj, String str) {
        Field declaredField = obj.getClass().getDeclaredField(str);
        declaredField.setAccessible(true);
        return declaredField.get(obj);
    }

    public static Object a(Object obj, String str, Class<?>[] clsArr, Object... objArr) {
        Method declaredMethod = obj.getClass().getDeclaredMethod(str, clsArr);
        declaredMethod.setAccessible(true);
        return declaredMethod.invoke(obj, objArr);
    }

    public static void a(Object obj, String str, Object obj2) {
        Field declaredField = obj.getClass().getDeclaredField(str);
        declaredField.setAccessible(true);
        declaredField.set(obj, obj2);
    }
}
