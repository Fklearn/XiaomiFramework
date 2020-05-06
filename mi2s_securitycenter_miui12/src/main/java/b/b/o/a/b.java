package b.b.o.a;

import android.os.IBinder;
import b.b.o.g.e;

public class b {
    public static IBinder a(String str) {
        Class[] clsArr = {String.class};
        try {
            return (IBinder) e.a(Class.forName("android.os.ServiceManager"), "getService", (Class<?>[]) clsArr, str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
