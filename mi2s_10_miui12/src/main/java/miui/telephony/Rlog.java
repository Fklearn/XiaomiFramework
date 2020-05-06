package miui.telephony;

import miui.provider.MiCloudSmsCmd;
import miui.reflect.Method;

class Rlog {
    private Rlog() {
    }

    public static void i(String tag, String msg) {
        try {
            Class cls = Class.forName("android.telephony.Rlog");
            Method.of(cls, "i", Integer.TYPE, new Class[]{String.class, String.class}).invoke(cls, (Object) null, new Object[]{tag, msg});
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void w(String tag, String msg) {
        try {
            Class cls = Class.forName("android.telephony.Rlog");
            Method.of(cls, MiCloudSmsCmd.TYPE_WIPE, Integer.TYPE, new Class[]{String.class, String.class}).invoke(cls, (Object) null, new Object[]{tag, msg});
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void e(String tag, String msg) {
        try {
            Class cls = Class.forName("android.telephony.Rlog");
            Method.of(cls, "e", Integer.TYPE, new Class[]{String.class, String.class}).invoke(cls, (Object) null, new Object[]{tag, msg});
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
