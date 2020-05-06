package miui.cloud.common;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public abstract class XCallback<T> {
    private Class<T> mInterface;

    public XCallback(Class<T> cls) {
        this.mInterface = cls;
    }

    public final T asInterface() {
        return Proxy.newProxyInstance(this.mInterface.getClassLoader(), new Class[]{this.mInterface}, new InvocationHandler() {
            public Object invoke(Object obj, Method method, Object[] objArr) {
                return XCallback.this.handleCallback(method, objArr);
            }
        });
    }

    /* access modifiers changed from: protected */
    public abstract Object handleCallback(Method method, Object[] objArr);
}
