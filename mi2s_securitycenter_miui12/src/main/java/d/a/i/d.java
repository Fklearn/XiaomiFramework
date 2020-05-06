package d.a.i;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class d {

    public interface a<T> {
        Object onMethod(Method method, Object[] objArr, T... tArr);

        boolean shouldIntercept(Method method, Object[] objArr);
    }

    public static <T> T a(Class<T> cls, a aVar, T... tArr) {
        c cVar = new c(aVar, tArr, cls);
        Object newProxyInstance = Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls}, cVar);
        if (cls.isInstance(newProxyInstance)) {
            return cls.cast(newProxyInstance);
        }
        return null;
    }
}
