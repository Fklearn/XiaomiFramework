package com.miui.optimizecenter.storage.d;

import android.text.TextUtils;
import android.util.Log;
import java.lang.reflect.Method;

public class b {
    public static Object a(Object obj, String str) {
        if (obj != null && !TextUtils.isEmpty(str)) {
            try {
                Method method = obj.getClass().getMethod(str, new Class[0]);
                method.setAccessible(true);
                return method.invoke(obj, new Object[0]);
            } catch (Exception e) {
                Log.e("ReflectUtils", "callObjectMethod: " + e.toString());
            }
        }
        return null;
    }
}
