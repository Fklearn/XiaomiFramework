package com.miui.permcenter.compact;

import b.b.o.g.e;
import java.lang.reflect.InvocationTargetException;

public class PrivacyCompat {
    public static final String TAG = "PrivacyCompat";

    public static String getClientPackageName(Object obj) {
        try {
            return (String) e.a(obj, String.class, "getClientPackageName", (Class<?>[]) new Class[0], new Object[0]);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return "";
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
            return "";
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
            return "";
        }
    }

    public static int getClientUid(Object obj) {
        try {
            return ((Integer) e.b(obj, Integer.TYPE, "getClientUid", new Class[0], new Object[0])).intValue();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return 0;
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
            return 0;
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
            return 0;
        }
    }
}
