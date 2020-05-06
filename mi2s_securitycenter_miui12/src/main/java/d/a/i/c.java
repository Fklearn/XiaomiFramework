package d.a.i;

import android.util.Log;
import d.a.i.d;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

class c implements InvocationHandler {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ d.a f8788a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Object[] f8789b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ Class f8790c;

    c(d.a aVar, Object[] objArr, Class cls) {
        this.f8788a = aVar;
        this.f8789b = objArr;
        this.f8790c = cls;
    }

    public Object invoke(Object obj, Method method, Object[] objArr) {
        Object obj2;
        d.a aVar = this.f8788a;
        if (aVar == null || !aVar.shouldIntercept(method, objArr)) {
            Object obj3 = null;
            for (Object obj4 : this.f8789b) {
                try {
                    obj3 = method.invoke(obj4, objArr);
                } catch (Exception e) {
                    Log.w("StyleComposer", "failed to invoke " + method + " for " + obj4, e.getCause());
                }
            }
            obj2 = obj3;
        } else {
            obj2 = this.f8788a.onMethod(method, objArr, this.f8789b);
        }
        if (obj2 != null) {
            Object[] objArr2 = this.f8789b;
            if (obj2 == objArr2[objArr2.length - 1]) {
                return this.f8790c.cast(obj);
            }
        }
        return obj2;
    }
}
