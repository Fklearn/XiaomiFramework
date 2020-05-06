package b.d.b.c;

import java.lang.reflect.Field;

public class f {
    public static int a(Class cls, String str) {
        try {
            Field declaredField = cls.getDeclaredField(str);
            declaredField.setAccessible(true);
            return declaredField.getInt((Object) null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Class a(String str) {
        try {
            return f.class.getClassLoader().loadClass(str);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
