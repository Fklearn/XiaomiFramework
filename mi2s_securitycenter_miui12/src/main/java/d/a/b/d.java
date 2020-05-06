package d.a.b;

import d.a.i.d;
import java.lang.reflect.Method;

class d implements d.a<l> {
    d() {
    }

    /* renamed from: a */
    public Object onMethod(Method method, Object[] objArr, l[] lVarArr) {
        if (lVarArr.length <= 0 || objArr.length <= 0) {
            return null;
        }
        a state = lVarArr[0].getState(objArr[0]);
        for (int i = 1; i < lVarArr.length; i++) {
            lVarArr[i].a(state);
        }
        return state;
    }

    public boolean shouldIntercept(Method method, Object[] objArr) {
        return method.getName().equals("getState");
    }
}
